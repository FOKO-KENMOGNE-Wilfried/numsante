package com.bank.numsante.controller;

import com.bank.numsante.dto.*;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.service.PersonnelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/personnel")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class PersonnelController {

    private final PersonnelService personnelService;

    @PostMapping
    @Operation(summary = "Créer un nouveau personnel médical (ADMIN)")
    public ResponseEntity<PersonnelMedical> creerPersonnel(@Valid @RequestBody CreatePersonnelRequest request,
                                                           Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(personnelService.creerPersonnel(request, authentication.getName()));
    }

    @GetMapping
    @Operation(summary = "Liste tout le personnel avec pagination")
    public ResponseEntity<PageResponse<PersonnelMedical>> getAllPersonnel(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(personnelService.getAllPersonnel(page, size, role));
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Personnel par rôle (medecin, infirmier, accueil, laborantin, pharmacien)")
    public ResponseEntity<List<PersonnelMedical>> getPersonnelByRole(@PathVariable String role) {
        return ResponseEntity.ok(personnelService.getPersonnelByRole(role));
    }

    @GetMapping("/hopital/{idHopital}")
    @Operation(summary = "Personnel par hôpital")
    public ResponseEntity<List<PersonnelMedical>> getPersonnelByHopital(@PathVariable Long idHopital) {
        return ResponseEntity.ok(personnelService.getPersonnelByHopital(idHopital));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un personnel (ADMIN)")
    public ResponseEntity<PersonnelMedical> updatePersonnel(@PathVariable Long id,
                                                            @Valid @RequestBody UpdatePersonnelRequest request) {
        return ResponseEntity.ok(personnelService.updatePersonnel(id, request));
    }

    @PutMapping("/reset-password")
    @Operation(summary = "Réinitialiser le mot de passe (ADMIN)")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        personnelService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Désactiver un personnel (ADMIN)")
    public ResponseEntity<Void> deletePersonnel(@PathVariable Long id) {
        personnelService.deletePersonnel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher un personnel par nom, prénom ou matricule")
    public ResponseEntity<List<PersonnelMedical>> searchPersonnel(@RequestParam String query) {
        return ResponseEntity.ok(personnelService.searchPersonnel(query));
    }
}