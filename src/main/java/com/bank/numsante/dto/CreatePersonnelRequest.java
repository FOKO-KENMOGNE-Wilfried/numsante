package com.bank.numsante.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreatePersonnelRequest {
    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank
    @Pattern(regexp = "MEDECIN|INFIRMIER|ACCUEIL|LABORANTIN|PHARMACIEN|ADMIN",
            message = "Rôle invalide")
    private String role;

    @NotBlank
    private String identifiantPro;

    @NotBlank
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @NotNull
    private Long idHopital;
}