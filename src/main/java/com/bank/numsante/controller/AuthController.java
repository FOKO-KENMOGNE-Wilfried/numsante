package com.bank.numsante.controller;

import com.bank.numsante.dto.BiometricLoginRequest;
import com.bank.numsante.dto.BiometricRegistrationRequest;
import com.bank.numsante.dto.LoginRequest;
import com.bank.numsante.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Connexion professionnel (identifiant/mot de passe)")
    @PostMapping("/login-professionnel")
    public ResponseEntity<Map<String, String>> loginProfessionnel(@Valid @RequestBody LoginRequest request) {
        String token = authService.loginProfessionnel(request);
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