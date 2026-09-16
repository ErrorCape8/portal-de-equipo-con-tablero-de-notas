package com.pruebatecnica.tablerodenotas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.LambdaClientBuilder;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    LambdaClient lambdaClient(
            @Value("${app.aws.region:us-east-1}") String region,
            @Value("${app.aws.lambda-endpoint:}") String lambdaEndpoint,
            @Value("${app.aws.access-key:}") String accessKey,
            @Value("${app.aws.secret-key:}") String secretKey) {

        LambdaClientBuilder builder = LambdaClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider(accessKey, secretKey));

        if (lambdaEndpoint != null && !lambdaEndpoint.isBlank()) {
            builder.endpointOverride(URI.create(lambdaEndpoint));
        }

        return builder.build();
    }

    private AwsCredentialsProvider credentialsProvider(String accessKey, String secretKey) {
        if (accessKey != null && !accessKey.isBlank()) {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        }
        return DefaultCredentialsProvider.create();
    }
}