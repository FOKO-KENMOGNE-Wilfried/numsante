package com.bank.numsante.controller;

import com.bank.numsante.dto.*;
import com.bank.numsante.entity.Patient;
import com.bank.numsante.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Enregistrer un nouveau patient")
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.registerPatient(request));
    }

    @Operation(summary = "Historique médical d’un patient")
    @GetMapping("/{idPatient}/historique")
    public ResponseEntity<List<HistoriquePassageDto>> getHistorique(@PathVariable UUID idPatient) {
        return ResponseEntity.ok(patientService.getHistorique(idPatient));
    }

    @Operation(summary = "Générer l'image QR code du patient")
    @GetMapping("/{idPatient}/qr-code")
    public ResponseEntity<byte[]> genererQRCode(@PathVariable UUID idPatient) {
        Patient patient = patientService.getPatientById(idPatient);
        if (patient.getCarteNumerique() == null) {
            throw new RuntimeException("Aucune carte QR associée à ce patient");
        }
        byte[] qrCodeImage = patientService.genererImageQR(patient.getCarteNumerique().getQrCodeToken());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCodeImage);
    }

    @Operation(summary = "Liste de tous les patients avec pagination et recherche")
    @GetMapping
    public ResponseEntity<PageResponse<Patient>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(patientService.getAllPatients(page, size, search));
    }

    @Operation(summary = "Détails d'un patient")
    @GetMapping("/{idPatient}")
    public ResponseEntity<Patient> getPatientById(@PathVariable UUID idPatient) {
        return ResponseEntity.ok(patientService.getPatientById(idPatient));
    }

    @Operation(summary = "Modifier les informations d'un patient")
    @PutMapping("/{idPatient}")
    public ResponseEntity<Patient> updatePatient(@PathVariable UUID idPatient,
                                                 @Valid @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(idPatient, request));
    }

    @Operation(summary = "Rechercher un patient par nom")
    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String query) {
        return ResponseEntity.ok(patientService.searchPatients(query));
    }

    @Operation(summary = "Renouveler la carte QR d'un patient")
    @PostMapping("/{idPatient}/renouveler-carte")
    public ResponseEntity<Map<String, Object>> renouvelerCarte(@PathVariable UUID idPatient) {
        return ResponseEntity.ok(patientService.renouvelerCarte(idPatient));
    }

    @Operation(summary = "Suspendre ou marquer comme perdue la carte QR")
    @PostMapping("/{idPatient}/suspendre-carte")
    public ResponseEntity<Map<String, Object>> suspendreCarte(
            @PathVariable UUID idPatient,
            @RequestParam(defaultValue = "suspendu") String motif) {
        return ResponseEntity.ok(patientService.suspendreCarte(idPatient, motif));
    }
}