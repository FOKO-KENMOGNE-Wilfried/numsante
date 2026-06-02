package com.bank.numsante.controller;

import com.bank.numsante.dto.PrescriptionValidationRequest;
import com.bank.numsante.entity.PrescriptionMedicament;
import com.bank.numsante.service.PharmacieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pharmacie")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class PharmacieController {

    private final PharmacieService pharmacieService;

    @PostMapping("/valider-prescription")
    @Operation(summary = "Valider et délivrer une prescription (PHARMACIEN)")
    public ResponseEntity<Map<String, String>> validerPrescription(
            @Valid @RequestBody PrescriptionValidationRequest request,
            Authentication authentication) {
        pharmacieService.validerPrescription(request, authentication.getName());
        return ResponseEntity.ok(Map.of("message",
                request.isDelivre() ? "Médicaments délivrés avec succès" : "Prescription refusée"));
    }

    @GetMapping("/prescriptions/{idPassage}")
    @Operation(summary = "Voir les prescriptions d'un passage")
    public ResponseEntity<List<PrescriptionMedicament>> getPrescriptions(@PathVariable UUID idPassage) {
        return ResponseEntity.ok(pharmacieService.getPrescriptionsParPassage(idPassage));
    }

    @GetMapping("/en-attente")
    @Operation(summary = "Liste des prescriptions en attente de délivrance")
    public ResponseEntity<List<PrescriptionMedicament>> getPrescriptionsEnAttente() {
        return ResponseEntity.ok(pharmacieService.getPrescriptionsEnAttente());
    }
}