package com.bank.numsante.controller;

import com.bank.numsante.dto.ExamenRequest;
import com.bank.numsante.entity.ExamenLaboratoire;
import com.bank.numsante.service.LaboratoireService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/laboratoire")
@RequiredArgsConstructor
public class LaboratoireController {

    private final LaboratoireService laboratoireService;

    @Operation(summary = "Ajouter des résultats d'examen")
    @PostMapping("/ajouter-examen")
    public ResponseEntity<Map<String, String>> ajouterExamen(@Valid @RequestBody ExamenRequest request,
                                                             Authentication authentication) {
        laboratoireService.ajouterExamen(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Résultats d'examen publiés sur le carnet du patient"));
    }

    @Operation(summary = "Récupérer les examens d'un passage médical")
    @GetMapping("/examens")
    public ResponseEntity<List<ExamenLaboratoire>> getExamensByPassage(
            @RequestParam UUID idPassage) {
        return ResponseEntity.ok(laboratoireService.getExamensByPassage(idPassage));
    }
}