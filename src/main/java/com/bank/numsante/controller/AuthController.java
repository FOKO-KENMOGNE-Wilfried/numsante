package com.bank.numsante.controller;

import com.bank.numsante.dto.*;
import com.bank.numsante.service.AuthService;
import com.bank.numsante.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints d'authentification (login classique, biométrique, enregistrement)")
public class AuthController {

    private final AuthService authService;
    private final PatientService patientService;

    @Operation(
        summary = "Connexion professionnel (identifiant/mot de passe)",
        description = "Authentifie un professionnel de santé avec son identifiant et mot de passe. Retourne un JWT et toutes les informations utilisateur."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "token": "eyJhbGciOiJIUzM4NCJ9...",
                      "idPersonnel": "1",
                      "id": "1",
                      "nom": "Mballa",
                      "prenom": "Dr",
                      "role": "medecin",
                      "identifiantPro": "dr_mballa",
                      "idHopital": "1",
                      "hopitalNom": "Hôpital Central",
                      "hopitalCode": "HC001"
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Identifiants invalides")
    })
    @PostMapping("/login-professionnel")
    public ResponseEntity<Map<String, String>> loginProfessionnel(@Valid @RequestBody LoginRequest request) {
        Map<String, String> response = authService.loginProfessionnel(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Enregistrement patient (création du compte)")
    @PostMapping("/register-patient")
    public ResponseEntity<Map<String, Object>> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        Map<String, Object> result = patientService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
        summary = "Connexion patient (email/mot de passe)",
        description = "Authentifie un patient avec son email et mot de passe. Retourne un JWT et toutes les informations du patient."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "token": "eyJhbGciOiJIUzM4NCJ9...",
                      "idPatient": "11111111-1111-1111-1111-111111111111",
                      "id": "11111111-1111-1111-1111-111111111111",
                      "nom": "Dupont",
                      "prenom": "Jean",
                      "email": "jean.dupont@email.com",
                      "role": "patient",
                      "dateNaissance": "1990-05-15",
                      "genre": "M",
                      "groupeSanguin": "O+",
                      "telephone": "0612345678",
                      "qrCode": "QR123456789"
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Email ou mot de passe incorrect")
    })
    @PostMapping("/login-patient")
    public ResponseEntity<Map<String, String>> loginPatient(@Valid @RequestBody LoginPatientRequest request) {
        Map<String, String> response = authService.loginPatient(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Enregistrement de la biométrie (clé publique)",
        description = "Enregistre la clé publique biométrique d'un utilisateur (patient ou professionnel). Accepte PATIENT, MEDECIN, INFIRMIER, ACCUEIL, etc."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Biométrie enregistrée avec succès",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "statut": "success",
                      "message": "Authentification biométrique configurée",
                      "idUtilisateur": "1"
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Erreur d'enregistrement",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "statut": "error",
                      "message": "Patient introuvable avec l'ID: 123"
                    }
                    """)
            )
        )
    })
    @PostMapping("/enregistrer-biometrie")
    public ResponseEntity<Map<String, String>> enregistrerBiometrie(@Valid @RequestBody BiometricRegistrationRequest request) {
        try {
            System.out.println("🔵 Controller: Réception requête enregistrement biométrie");
            System.out.println("   - ID: " + request.getIdUtilisateur());
            System.out.println("   - Type: " + request.getTypeUtilisateur());

            authService.enregistrerBiometrie(request);

            return ResponseEntity.ok(Map.of(
                "statut", "success",
                "message", "Authentification biométrique configurée",
                "idUtilisateur", request.getIdUtilisateur()
            ));
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur enregistrement biométrie: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "statut", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "statut", "error",
                "message", "Erreur interne du serveur"
            ));
        }
    }

    @Operation(
        summary = "Connexion biométrique",
        description = "Authentifie un utilisateur (patient ou professionnel) via biométrie. Retourne un JWT et toutes les informations utilisateur, identique au login classique."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion biométrique patient réussie",
            content = @Content(mediaType = "application/json",
                examples = {
                    @ExampleObject(name = "Patient", value = """
                        {
                          "token": "eyJhbGciOiJIUzM4NCJ9...",
                          "idPatient": "11111111-1111-1111-1111-111111111111",
                          "id": "11111111-1111-1111-1111-111111111111",
                          "nom": "Dupont",
                          "prenom": "Jean",
                          "email": "jean.dupont@email.com",
                          "role": "patient",
                          "dateNaissance": "1990-05-15",
                          "genre": "M",
                          "groupeSanguin": "O+",
                          "telephone": "0612345678",
                          "qrCode": "QR123456789"
                        }
                        """),
                    @ExampleObject(name = "Professionnel", value = """
                        {
                          "token": "eyJhbGciOiJIUzM4NCJ9...",
                          "idPersonnel": "1",
                          "id": "1",
                          "nom": "Mballa",
                          "prenom": "Dr",
                          "role": "medecin",
                          "identifiantPro": "dr_mballa",
                          "idHopital": "1",
                          "hopitalNom": "Hôpital Central",
                          "hopitalCode": "HC001"
                        }
                        """)
                }
            )
        ),
        @ApiResponse(responseCode = "401", description = "Authentification biométrique échouée",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "statut": "error",
                      "message": "Authentification biométrique échouée"
                    }
                    """)
            )
        )
    })
    @PostMapping("/login-biometrique")
    public ResponseEntity<Map<String, String>> loginBiometrique(@Valid @RequestBody BiometricLoginRequest request) {
        try {
            System.out.println("🔵 Controller: Réception requête login biométrique");
            System.out.println("   - ID: " + request.getIdUtilisateur());

            // ✅ Retourner toutes les infos utilisateur (pas seulement le token)
            Map<String, String> response = authService.loginBiometrique(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur login biométrique: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body(Map.of(
                "statut", "error",
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "statut", "error",
                "message", "Erreur interne du serveur"
            ));
        }
    }
}