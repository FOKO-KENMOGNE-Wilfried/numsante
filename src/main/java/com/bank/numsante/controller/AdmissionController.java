package com.bank.numsante.controller;

import com.bank.numsante.dto.CreerPassageRequest;
import com.bank.numsante.dto.PatientInfoDto;
import com.bank.numsante.dto.QrScanRequest;
import com.bank.numsante.service.AdmissionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admission")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionService admissionService;

    @Operation(summary = "Scanner une carte QR et identifier le patient")
    @PostMapping("/scan-carte")
    public ResponseEntity<PatientInfoDto> scanCarte(@Valid @RequestBody QrScanRequest request) {
        return ResponseEntity.ok(admissionService.scanCarte(request));
    }

    @Operation(summary = "Créer un nouveau passage médical (admission)")
    @PostMapping("/creer-passage")
    public ResponseEntity<Map<String, Object>> creerPassage(@Valid @RequestBody CreerPassageRequest request,
                                                            Authentication authentication) {
        UUID idPassage = admissionService.creerPassage(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id_passage", idPassage, "statut", "en_cours"));
    }
}