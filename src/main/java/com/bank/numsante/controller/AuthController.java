package com.bank.numsante.controller;

import com.bank.numsante.dto.*;
import com.bank.numsante.service.AuthService;
import com.bank.numsante.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PatientService patientService;

    @Operation(summary = "Connexion professionnel (identifiant/mot de passe)")
    @PostMapping("/login-professionnel")
    public ResponseEntity<Map<String, String>> loginProfessionnel(@Valid @RequestBody LoginRequest request) {
        String token = authService.loginProfessionnel(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Operation(summary = "Enregistrement patient (création du compte)")
    @PostMapping("/register-patient")
    public ResponseEntity<Map<String, Object>> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        Map<String, Object> result = patientService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Connexion patient (email/mot de passe)")
    @PostMapping("/login-patient")
    public ResponseEntity<Map<String, String>> loginPatient(@Valid @RequestBody LoginPatientRequest request) {
        String token = authService.loginPatient(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Operation(summary = "Enregistrement de la biométrie (clé publique)")
    @PostMapping("/enregistrer-biometrie")
    public ResponseEntity<Map<String, String>> enregistrerBiometrie(@Valid @RequestBody BiometricRegistrationRequest request) {
        authService.enregistrerBiometrie(request);
        return ResponseEntity.ok(Map.of("statut", "success", "message", "Authentification biométrique configurée"));
    }

    @Operation(summary = "Connexion biométrique (simulée)")
    @PostMapping("/login-biometrique")
    public ResponseEntity<Map<String, String>> loginBiometrique(@Valid @RequestBody BiometricLoginRequest request) {
        String token = authService.loginBiometrique(request);
        return ResponseEntity.ok(Map.of("token", token));
    }
}