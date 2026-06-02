package com.bank.numsante.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateHopitalRequest {
    @NotBlank
    private String nom;

    @NotBlank
    private String adresse;

    @NotBlank
    private String codeUnique;
}