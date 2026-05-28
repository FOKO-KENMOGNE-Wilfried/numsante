package com.bank.numsante.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreerPassageRequest {
    @NotNull
    private String idPatient; // UUID
    @NotNull
    private Long idHopital;
    @NotBlank
    private String motifVisite;
}