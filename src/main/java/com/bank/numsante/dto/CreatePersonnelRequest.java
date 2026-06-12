package com.bank.numsante.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreatePersonnelRequest {
    @NotBlank
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @NotBlank
    @Pattern(regexp = "MEDECIN|INFIRMIER|ACCUEIL|LABORANTIN|PHARMACIEN|ADMIN",
            message = "Rôle invalide")
    private String role;

    @NotBlank
    @Pattern(regexp = "^[a-z0-9_]+$", message = "Identifiant invalide (minuscules, chiffres, underscore uniquement)")
    private String identifiantPro;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @NotNull
    private Long idHopital;
}