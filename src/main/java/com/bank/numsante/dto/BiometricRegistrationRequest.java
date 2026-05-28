package com.bank.numsante.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BiometricRegistrationRequest {
    @NotBlank
    private String idUtilisateur;
    @NotBlank
    private String typeUtilisateur; // "patient" ou "personnel"
    @NotBlank
    private String clePubliqueAppareil;
}