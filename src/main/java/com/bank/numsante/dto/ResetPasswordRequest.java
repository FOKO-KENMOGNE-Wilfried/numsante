package com.bank.numsante.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String identifiantPro;

    @NotBlank
    @Size(min = 8, message = "Le nouveau mot de passe doit contenir au moins 8 caractères")
    private String nouveauMotDePasse;
}