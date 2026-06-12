package com.bank.numsante.controller;

import com.bank.numsante.dto.CreateHopitalRequest;
import com.bank.numsante.entity.Hopital;
import com.bank.numsante.service.HopitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/hopitaux")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class HopitalController {

    private final HopitalService hopitalService;

    @PostMapping
    @Operation(summary = "Créer un nouvel hôpital (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hopital> creerHopital(@Valid @RequestBody CreateHopitalRequest request,
                                                Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hopitalService.creerHopital(request, authentication.getName()));
    }

    @GetMapping
    @Operation(summary = "Liste tous les hôpitaux")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Hopital>> getAllHopitaux() {
        return ResponseEntity.ok(hopitalService.getAllHopitaux());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un hôpital")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Hopital> getHopitalById(@PathVariable Long id) {
        return ResponseEntity.ok(hopitalService.getHopitalById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un hôpital (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Hopital> updateHopital(@PathVariable Long id,
                                                 @Valid @RequestBody CreateHopitalRequest request) {
        return ResponseEntity.ok(hopitalService.updateHopital(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un hôpital (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHopital(@PathVariable Long id) {
        hopitalService.deleteHopital(id);
        return ResponseEntity.noContent().build();
    }
}