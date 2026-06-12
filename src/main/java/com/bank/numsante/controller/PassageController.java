package com.bank.numsante.controller;

import com.bank.numsante.dto.ConstantesVitalesRequest;
import com.bank.numsante.dto.ConsultationRequest;
import com.bank.numsante.dto.PageResponse;
import com.bank.numsante.entity.PassageMedical;
import com.bank.numsante.service.PassageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/passages")
@RequiredArgsConstructor
public class PassageController {

    private final PassageService passageService;

    @Operation(summary = "Mettre à jour les constantes vitales d’un passage")
    @PutMapping("/{idPassage}/constantes")
    public ResponseEntity<PassageMedical> updateConstantes(
            @PathVariable UUID idPassage,
            @Valid @RequestBody ConstantesVitalesRequest request,
            Authentication authentication) {

        PassageMedical updatedPassage = passageService.updateConstantes(idPassage, request, authentication.getName());
        return ResponseEntity.ok(updatedPassage);
    }

    @Operation(summary = "Ajouter diagnostic / prescription et éventuellement clôturer le passage")
    @PutMapping("/{idPassage}/consultation")
    public ResponseEntity<PassageMedical> ajouterConsultation(
            @PathVariable UUID idPassage,
            @Valid @RequestBody ConsultationRequest request,
            Authentication authentication) {

        PassageMedical updatedPassage = passageService.ajouterConsultation(idPassage, request, authentication.getName());
        return ResponseEntity.ok(updatedPassage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un passage médical par son ID")
    public ResponseEntity<PassageMedical> getPassageById(@PathVariable UUID id) {
        return ResponseEntity.ok(passageService.getPassageById(id));
    }

    @GetMapping("/hopital/{idHopital}")
    @Operation(summary = "Passages par hôpital avec pagination")
    public ResponseEntity<PageResponse<PassageMedical>> getPassagesByHopital(
            @PathVariable Long idHopital,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(passageService.getPassagesByHopital(idHopital, page, size));
    }

    @GetMapping("/en-cours/{idHopital}")
    @Operation(summary = "Passages en cours dans un hôpital")
    public ResponseEntity<List<PassageMedical>> getPassagesEnCours(@PathVariable Long idHopital) {
        return ResponseEntity.ok(passageService.getPassagesEnCours(idHopital));
    }

    @DeleteMapping("/{idPassage}")
    @Operation(summary = "Annuler un passage")
    public ResponseEntity<PassageMedical> annulerPassage(@PathVariable UUID idPassage, Authentication authentication) {
        PassageMedical annulledPassage = passageService.annulerPassage(idPassage, authentication.getName());
        return ResponseEntity.ok(annulledPassage);
    }
}