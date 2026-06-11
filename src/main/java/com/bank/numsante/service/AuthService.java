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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PersonnelMedicalRepository personnelRepo;
    private final PatientRepository patientRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String loginPatient(LoginPatientRequest request) {
        Patient patient = patientRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), patient.getMotDePasseHash())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        return jwtTokenProvider.generateToken(patient.getEmail(), "PATIENT");
    }

    public String loginProfessionnel(LoginRequest request) {
        PersonnelMedical personnel = personnelRepo.findByIdentifiantPro(request.getIdentifiantPro())
                .orElseThrow(() -> new RuntimeException("Identifiants invalides"));
        if (!passwordEncoder.matches(request.getMotDePasse(), personnel.getMotDePasseHash())) {
            throw new RuntimeException("Identifiants invalides");
        }
        return jwtTokenProvider.generateToken(personnel.getIdentifiantPro(), personnel.getRole());
    }

    public void enregistrerBiometrie(BiometricRegistrationRequest request) {
        if ("patient".equals(request.getTypeUtilisateur())) {
            Patient patient = patientRepo.findById(java.util.UUID.fromString(request.getIdUtilisateur()))
                    .orElseThrow(() -> new RuntimeException("Patient introuvable"));
            patient.setClePubliqueBiometrique(request.getClePubliqueAppareil());
            patientRepo.save(patient);
        } else if ("personnel".equals(request.getTypeUtilisateur())) {
            PersonnelMedical personnel = personnelRepo.findById(Long.parseLong(request.getIdUtilisateur()))
                    .orElseThrow(() -> new RuntimeException("Personnel introuvable"));
            personnel.setClePubliqueAppareil(request.getClePubliqueAppareil());
            personnelRepo.save(personnel);
        } else {
            throw new RuntimeException("Type utilisateur invalide");
        }
    }

    // Simulation de connexion biométrique : on vérifie que la clé publique existe, puis on génère un token.
    public String loginBiometrique(BiometricLoginRequest request) {
        // Chercher d'abord dans les patients
        try {
            Patient patient = patientRepo.findById(java.util.UUID.fromString(request.getIdUtilisateur())).orElse(null);
            if (patient != null && patient.getClePubliqueBiometrique() != null) {
                // Dans un cas réel, on vérifierait la signature avec la clé publique.
                // Ici on simule en acceptant toute signature non vide.
                if (!request.getSignatureDefi().isBlank()) {
                    return jwtTokenProvider.generateToken(patient.getIdPatient().toString(), "PATIENT");
                }
            }
        } catch (IllegalArgumentException ignored) {}
        // Chercher dans le personnel
        PersonnelMedical personnel = personnelRepo.findById(Long.parseLong(request.getIdUtilisateur()))
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        if (personnel.getClePubliqueAppareil() != null && !request.getSignatureDefi().isBlank()) {
            return jwtTokenProvider.generateToken(personnel.getIdentifiantPro(), personnel.getRole());
        }
        throw new RuntimeException("Authentification biométrique échouée");
    }
}