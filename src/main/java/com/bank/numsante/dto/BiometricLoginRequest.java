package com.bank.numsante.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BiometricLoginRequest {
    @NotBlank
    private String idUtilisateur;
    @NotBlank
    private String signatureDefi;
}