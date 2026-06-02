package com.bank.numsante.controller;

import com.bank.numsante.dto.DashboardStats;
import com.bank.numsante.service.RapportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rapports")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class RapportController {

    private final RapportService rapportService;

    @GetMapping("/dashboard")
    @Operation(summary = "Statistiques du tableau de bord (ADMIN, MEDECIN)")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(rapportService.getDashboardStats());
    }
}