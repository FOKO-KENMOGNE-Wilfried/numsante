package com.bank.numsante.service;

import com.bank.numsante.config.JwtTokenProvider;
import com.bank.numsante.dto.BiometricLoginRequest;
import com.bank.numsante.dto.BiometricRegistrationRequest;
import com.bank.numsante.dto.LoginPatientRequest;
import com.bank.numsante.dto.LoginRequest;
import com.bank.numsante.entity.Patient;
import com.bank.numsante.entity.PersonnelMedical;
import com.bank.numsante.repository.PatientRepository;
import com.bank.numsante.repository.PersonnelMedicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PersonnelMedicalRepository personnelRepo;
    private final PatientRepository patientRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, String> loginPatient(LoginPatientRequest request) {
        Patient patient = patientRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), patient.getMotDePasseHash())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        String token = jwtTokenProvider.generateToken(patient.getEmail(), "PATIENT");

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("idPatient", patient.getIdPatient().toString());
        response.put("id", patient.getIdPatient().toString()); // Alias pour le frontend
        response.put("nom", patient.getNom());
        response.put("prenom", patient.getPrenom());
        response.put("email", patient.getEmail());
        response.put("role", "patient");

        // Informations supplémentaires
        if (patient.getDateNaissance() != null) {
            response.put("dateNaissance", patient.getDateNaissance().toString());
        }
        if (patient.getGenre() != null) {
            response.put("genre", patient.getGenre().toString());
        }
        if (patient.getGroupeSanguin() != null) {
            response.put("groupeSanguin", patient.getGroupeSanguin());
        }
        if (patient.getTelephone() != null) {
            response.put("telephone", patient.getTelephone());
        }

        // QR Code depuis la carte numérique
        if (patient.getCarteNumerique() != null && patient.getCarteNumerique().getQrCodeToken() != null) {
            response.put("qrCode", patient.getCarteNumerique().getQrCodeToken());
        }

        return response;
    }

    public Map<String, String> loginProfessionnel(LoginRequest request) {
        PersonnelMedical personnel = personnelRepo.findByIdentifiantPro(request.getIdentifiantPro())
                .orElseThrow(() -> new RuntimeException("Identifiants invalides"));
        if (!passwordEncoder.matches(request.getMotDePasse(), personnel.getMotDePasseHash())) {
            throw new RuntimeException("Identifiants invalides");
        }

        String token = jwtTokenProvider.generateToken(personnel.getIdentifiantPro(), personnel.getRole());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("idPersonnel", String.valueOf(personnel.getIdPersonnel()));
        response.put("id", String.valueOf(personnel.getIdPersonnel())); // Alias pour le frontend
        response.put("nom", personnel.getNom());
        response.put("prenom", personnel.getPrenom());
        response.put("role", personnel.getRole().toLowerCase());
        response.put("identifiantPro", personnel.getIdentifiantPro());

        // Informations de l'hôpital (si disponible)
        if (personnel.getHopital() != null) {
            response.put("idHopital", String.valueOf(personnel.getHopital().getIdHopital()));
            response.put("hopitalNom", personnel.getHopital().getNom());
            response.put("hopitalCode", personnel.getHopital().getCodeUnique());
        }

        return response;
    }

    public void enregistrerBiometrie(BiometricRegistrationRequest request) {
        // Log pour debug
        System.out.println("========================================");
        System.out.println("📱 Enregistrement biométrie:");
        System.out.println("   - ID utilisateur: " + request.getIdUtilisateur());
        System.out.println("   - Type: " + request.getTypeUtilisateur());
        System.out.println("   - Clé (début): " +
            (request.getClePubliqueAppareil() != null && request.getClePubliqueAppareil().length() > 20
                ? request.getClePubliqueAppareil().substring(0, 20) + "..."
                : request.getClePubliqueAppareil()));
        System.out.println("========================================");

        // Accepter "PATIENT" ou "patient" (case insensitive)
        boolean isPatient = "patient".equalsIgnoreCase(request.getTypeUtilisateur());

        if (isPatient) {
            // Enregistrement pour un patient
            Patient patient = patientRepo.findById(java.util.UUID.fromString(request.getIdUtilisateur()))
                    .orElseThrow(() -> new RuntimeException("Patient introuvable avec l'ID: " + request.getIdUtilisateur()));
            patient.setClePubliqueBiometrique(request.getClePubliqueAppareil());
            patientRepo.save(patient);
            System.out.println("✅ Biométrie enregistrée pour le patient");
        } else {
            // Tous les autres types (MEDECIN, INFIRMIER, ACCUEIL, PERSONNEL, etc.) sont du personnel
            try {
                PersonnelMedical personnel = personnelRepo.findById(Long.parseLong(request.getIdUtilisateur()))
                        .orElseThrow(() -> new RuntimeException("Personnel introuvable avec l'ID: " + request.getIdUtilisateur()));
                personnel.setClePubliqueAppareil(request.getClePubliqueAppareil());
                personnelRepo.save(personnel);
                System.out.println("✅ Biométrie enregistrée pour le personnel (" + request.getTypeUtilisateur() + ")");
            } catch (NumberFormatException e) {
                throw new RuntimeException("Format d'ID invalide pour le personnel. ID fourni: " + request.getIdUtilisateur());
            }
        }
    }

    // Simulation de connexion biométrique : on vérifie que la clé publique existe, puis on génère un token.
    public Map<String, String> loginBiometrique(BiometricLoginRequest request) {
        System.out.println("========================================");
        System.out.println("🔐 Connexion biométrique:");
        System.out.println("   - ID utilisateur: " + request.getIdUtilisateur());
        System.out.println("========================================");

        // Chercher d'abord dans les patients
        try {
            Patient patient = patientRepo.findById(java.util.UUID.fromString(request.getIdUtilisateur())).orElse(null);
            if (patient != null && patient.getClePubliqueBiometrique() != null) {
                // Dans un cas réel, on vérifierait la signature avec la clé publique.
                // Ici on simule en acceptant toute signature non vide.
                if (!request.getSignatureDefi().isBlank()) {
                    System.out.println("✅ Patient trouvé: " + patient.getEmail());

                    // ✅ Utiliser l'email du patient (pas l'UUID) pour le JWT
                    String token = jwtTokenProvider.generateToken(patient.getEmail(), "PATIENT");

                    // ✅ Retourner toutes les infos utilisateur (COMPLÈTES)
                    Map<String, String> response = new HashMap<>();
                    response.put("token", token);
                    response.put("idPatient", patient.getIdPatient().toString());
                    response.put("id", patient.getIdPatient().toString()); // Alias pour le frontend
                    response.put("nom", patient.getNom());
                    response.put("prenom", patient.getPrenom());
                    response.put("email", patient.getEmail());
                    response.put("role", "patient");

                    // Informations supplémentaires
                    if (patient.getDateNaissance() != null) {
                        response.put("dateNaissance", patient.getDateNaissance().toString());
                    }
                    if (patient.getGenre() != null) {
                        response.put("genre", patient.getGenre().toString());
                    }
                    if (patient.getGroupeSanguin() != null) {
                        response.put("groupeSanguin", patient.getGroupeSanguin());
                    }
                    if (patient.getTelephone() != null) {
                        response.put("telephone", patient.getTelephone());
                    }

                    // QR Code depuis la carte numérique
                    if (patient.getCarteNumerique() != null && patient.getCarteNumerique().getQrCodeToken() != null) {
                        response.put("qrCode", patient.getCarteNumerique().getQrCodeToken());
                    }

                    System.out.println("✅ Connexion biométrique patient réussie");
                    return response;
                }
            }
        } catch (IllegalArgumentException ignored) {
            System.out.println("⚠️ ID utilisateur n'est pas un UUID patient valide, recherche dans le personnel...");
        }

        // Chercher dans le personnel
        try {
            PersonnelMedical personnel = personnelRepo.findById(Long.parseLong(request.getIdUtilisateur()))
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            if (personnel.getClePubliqueAppareil() != null && !request.getSignatureDefi().isBlank()) {
                System.out.println("✅ Personnel trouvé: " + personnel.getIdentifiantPro());

                String token = jwtTokenProvider.generateToken(personnel.getIdentifiantPro(), personnel.getRole());

                // ✅ Retourner toutes les infos utilisateur (COMPLÈTES)
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("idPersonnel", String.valueOf(personnel.getIdPersonnel()));
                response.put("id", String.valueOf(personnel.getIdPersonnel())); // Alias pour le frontend
                response.put("nom", personnel.getNom());
                response.put("prenom", personnel.getPrenom());
                response.put("role", personnel.getRole().toLowerCase());
                response.put("identifiantPro", personnel.getIdentifiantPro());

                // Informations de l'hôpital (si disponible)
                if (personnel.getHopital() != null) {
                    response.put("idHopital", String.valueOf(personnel.getHopital().getIdHopital()));
                    response.put("hopitalNom", personnel.getHopital().getNom());
                    response.put("hopitalCode", personnel.getHopital().getCodeUnique());
                }

                System.out.println("✅ Connexion biométrique personnel réussie");
                return response;
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Format d'ID invalide pour le personnel");
        }

        System.err.println("❌ Authentification biométrique échouée");
        throw new RuntimeException("Authentification biométrique échouée");
    }
}