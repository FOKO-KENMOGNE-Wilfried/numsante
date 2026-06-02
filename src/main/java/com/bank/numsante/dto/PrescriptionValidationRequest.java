package com.bank.numsante.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PrescriptionValidationRequest {
    @NotBlank
    private String idPassage; // UUID
    private boolean delivre;
    private String commentaire;
}