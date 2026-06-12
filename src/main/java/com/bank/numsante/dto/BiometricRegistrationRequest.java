package com.bank.numsante.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BiometricRegistrationRequest {
    @NotBlank
    private String idUtilisateur;

    @NotBlank
    // Accepte: "PATIENT" (ou "patient") pour les patients
    // Accepte: "MEDECIN", "INFIRMIER", "ACCUEIL", "LABORANTIN", "PHARMACIEN", "ADMIN", "PERSONNEL" pour le personnel médical
    private String typeUtilisateur;

    @NotBlank
    private String clePubliqueAppareil;
}