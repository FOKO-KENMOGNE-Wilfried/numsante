package com.bank.numsante.controller;

import com.bank.numsante.entity.LogTracabilite;
import com.bank.numsante.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
@Tag(name = "Logs", description = "API de traçabilité des actions")
public class LogController {

    private final LogService logService;

    @GetMapping
    @Operation(summary = "Récupérer tous les logs avec pagination et filtres optionnels")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN')")
    public ResponseEntity<Page<LogTracabilite>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(logService.getAllLogs(page, size, action));
    }

    @GetMapping("/patient/{idPatient}")
    @Operation(summary = "Récupérer les logs d'un patient spécifique")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN')")
    public ResponseEntity<Page<LogTracabilite>> getLogsByPatient(
            @PathVariable UUID idPatient,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logService.getLogsByPatient(idPatient, page, size));
    }

    @GetMapping("/passage/{idPassage}")
    @Operation(summary = "Récupérer les logs d'un passage médical spécifique")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDECIN')")
    public ResponseEntity<List<LogTracabilite>> getLogsByPassage(
            @PathVariable UUID idPassage) {
        return ResponseEntity.ok(logService.getLogsByPassage(idPassage));
    }

    @GetMapping("/personnel/{idPersonnel}")
    @Operation(summary = "Récupérer les logs d'un personnel médical spécifique")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<LogTracabilite>> getLogsByPersonnel(
            @PathVariable Long idPersonnel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logService.getLogsByPersonnel(idPersonnel, page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Recherche avancée de logs avec filtres multiples")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<LogTracabilite>> searchLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) UUID idPatient,
            @RequestParam(required = false) Long idUtilisateur,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logService.searchLogs(action, idPatient, idUtilisateur, page, size));
    }
}
