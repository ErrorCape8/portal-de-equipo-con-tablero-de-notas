package com.pruebatecnica.tablerodenotas.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pruebatecnica.tablerodenotas.Repo.NoteRepo;
import com.pruebatecnica.tablerodenotas.entidades.Note;
import com.pruebatecnica.tablerodenotas.entidades.NotesStatus;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepo noteRepo;

    public NoteController(NoteRepo noteRepo) {
        this.noteRepo = noteRepo;
    }

    public record NoteRequest(String title, String text, String status, Double positionX, Double positionY) {
    }

    public record PositionRequest(double positionX, double positionY) {
    }

    public record NoteResponse(Long id, String title, String text, NotesStatus status, double positionX,
            double positionY) {
        static NoteResponse from(Note note) {
            return new NoteResponse(note.getId(), note.getTitle(), note.getText(), note.getStatus(),
                    note.getPositionX(), note.getPositionY());
        }
    }

    @GetMapping
    public List<NoteResponse> list() {
        return noteRepo.findAll().stream().map(NoteResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponse create(@RequestBody NoteRequest request) {
        Note note = Note.builder()
                .title(required(request.title(), "El titulo es obligatorio"))
                .text(request.text())
                .status(parseStatus(request.status()))
                .positionX(requiredPosition(request.positionX(), "positionX"))
                .positionY(requiredPosition(request.positionY(), "positionY"))
                .build();
        return NoteResponse.from(noteRepo.save(note));
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable Long id, @RequestBody NoteRequest request) {
        Note note = findById(id);
        note.setTitle(required(request.title(), "El titulo es obligatorio"));
        note.setText(request.text());
        note.setStatus(parseStatus(request.status()));
        note.setPositionX(requiredPosition(request.positionX(), "positionX"));
        note.setPositionY(requiredPosition(request.positionY(), "positionY"));
        return NoteResponse.from(noteRepo.save(note));
    }

    @PatchMapping("/{id}/position")
    public NoteResponse move(@PathVariable Long id, @RequestBody PositionRequest request) {
        Note note = findById(id);
        note.setPositionX(request.positionX());
        note.setPositionY(request.positionY());
        return NoteResponse.from(noteRepo.save(note));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        noteRepo.delete(findById(id));
    }

    private Note findById(Long id) {
        return noteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nota no encontrada"));
    }

    private NotesStatus parseStatus(String status) {
        String normalizedStatus = required(status, "El estado es obligatorio").toUpperCase(Locale.ROOT);
        try {
            return NotesStatus.valueOf(normalizedStatus);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("El estado debe ser PENDIENTE, EN_PROCESO o COMPLETADO");
        }
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private double requiredPosition(Double value, String field) {
        if (value == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException(field + " debe ser un numero valido");
        }
        return value;
    }
}