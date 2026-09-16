package com.pruebatecnica.tablerodenotas.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pruebatecnica.tablerodenotas.Repo.NoteRepo;
import com.pruebatecnica.tablerodenotas.entidades.Note;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DashboardMetricsService {

    private static final Logger log = LoggerFactory.getLogger(DashboardMetricsService.class);

    private final NoteRepo noteRepo;
    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;
    private final String functionName;

    public DashboardMetricsService(
            NoteRepo noteRepo,
            LambdaClient lambdaClient,
            ObjectMapper objectMapper,
            @Value("${app.aws.lambda.dashboard-metrics-function:dashboard-metrics}") String functionName) {
        this.noteRepo = noteRepo;
        this.lambdaClient = lambdaClient;
        this.objectMapper = objectMapper;
        this.functionName = functionName;
    }

    public DashboardMetrics calculate() {
        try {
            String requestPayload = objectMapper.writeValueAsString(new MetricsRequest(
                    noteRepo.findAll().stream().map(NoteInput::from).toList()));
            InvokeResponse response = lambdaClient.invoke(InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(SdkBytes.fromString(requestPayload, StandardCharsets.UTF_8))
                    .build());

            if (response.functionError() != null) {
                throw new IllegalStateException("La Lambda de metricas devolvio un error: " + response.functionError());
            }
            LambdaMetrics metrics = objectMapper.readValue(response.payload().asUtf8String(), LambdaMetrics.class);
            return new DashboardMetrics(metrics.total(), metrics.pending(), metrics.progress(), metrics.done(), true,
                    null);
        } catch (Exception exception) {
            log.error("No se pudieron calcular las metricas via Lambda", exception);
            return DashboardMetrics.unavailable();
        }
    }

    private record MetricsRequest(List<NoteInput> notes) {
    }

    private record NoteInput(String status) {
        static NoteInput from(Note note) {
            return new NoteInput(note.getStatus().name());
        }
    }

    private record LambdaMetrics(long total, long pending, long progress, long done) {
    }

    public record DashboardMetrics(long total, long pending, long progress, long done, boolean available,
            String message) {
        static DashboardMetrics unavailable() {
            return new DashboardMetrics(0, 0, 0, 0, false, "Metricas no disponibles");
        }
    }
}