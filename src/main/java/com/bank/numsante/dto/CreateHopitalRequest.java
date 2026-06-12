package com.bank.numsante.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateHopitalRequest {
    @NotBlank
    @Size(max = 200, message = "Le nom ne doit pas dépasser 200 caractères")
    private String nom;

    @NotBlank
    @Size(max = 300, message = "L'adresse ne doit pas dépasser 300 caractères")
    private String adresse;

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "Code invalide (majuscules, chiffres, tirets uniquement)")
    private String codeUnique;
}