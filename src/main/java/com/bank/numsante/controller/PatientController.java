package com.bank.numsante.controller;

import com.bank.numsante.dto.EnregistrementPatientRequest;
import com.bank.numsante.dto.HistoriquePassageDto;
import com.bank.numsante.dto.PageResponse;
import com.bank.numsante.dto.UpdatePatientRequest;
import com.bank.numsante.entity.Patient;
import com.bank.numsante.exception.ResourceNotFoundException;
import com.bank.numsante.repository.PatientRepository;
import com.bank.numsante.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
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
public class PatientController {

    private final PatientService patientService;
    private final PatientRepository patientRepository;

    @GetMapping("/{idPatient}/qr-code")
    @Operation(summary = "Générer l'image QR code du patient")
    public ResponseEntity<byte[]> genererQRCode(@PathVariable UUID idPatient) {
        Patient patient = patientRepository.findById(idPatient)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));

        if (patient.getCarteNumerique() == null) {
            throw new RuntimeException("Aucune carte QR associée à ce patient");
        }

        byte[] qrCodeImage = patientService.genererImageQR(patient.getCarteNumerique().getQrCodeToken());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCodeImage);
    }

    @PostMapping("/enregistrer")
    @Operation(summary = "Enregistrer un nouveau patient avec sa carte QR")
    public ResponseEntity<Map<String, Object>> enregistrerPatient(
            @Valid @RequestBody EnregistrementPatientRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.enregistrerPatient(request, authentication.getName()));
    }

    @Operation(summary = "Historique médical d’un patient")
    @GetMapping("/{idPatient}/historique")
    public ResponseEntity<List<HistoriquePassageDto>> getHistorique(@PathVariable UUID idPatient,
                                                                    Authentication authentication) {
        return ResponseEntity.ok(patientService.getHistorique(idPatient, authentication.getName()));
    }

    @GetMapping
    @Operation(summary = "Liste de tous les patients avec pagination et recherche")
    public ResponseEntity<PageResponse<Patient>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(patientService.getAllPatients(page, size, search));
    }

    @GetMapping("/{idPatient}")
    @Operation(summary = "Détails d'un patient")
    public ResponseEntity<Patient> getPatientById(@PathVariable UUID idPatient) {
        return ResponseEntity.ok(patientService.getPatientById(idPatient));
    }

    @PutMapping("/{idPatient}")
    @Operation(summary = "Modifier les informations d'un patient")
    public ResponseEntity<Patient> updatePatient(@PathVariable UUID idPatient,
                                                 @Valid @RequestBody UpdatePatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(idPatient, request));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher un patient par nom")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String query) {
        return ResponseEntity.ok(patientService.searchPatients(query));
    }

    @PostMapping("/{idPatient}/renouveler-carte")
    @Operation(summary = "Renouveler la carte QR d'un patient")
    public ResponseEntity<Map<String, Object>> renouvelerCarte(@PathVariable UUID idPatient) {
        return ResponseEntity.ok(patientService.renouvelerCarte(idPatient));
    }

    @PostMapping("/{idPatient}/suspendre-carte")
    @Operation(summary = "Suspendre ou marquer comme perdue la carte QR")
    public ResponseEntity<Map<String, Object>> suspendreCarte(
            @PathVariable UUID idPatient,
            @RequestParam(defaultValue = "suspendu") String motif) {
        return ResponseEntity.ok(patientService.suspendreCarte(idPatient, motif));
    }
}