package com.bank.numsante.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamenRequest {
    @NotNull
    private String idPassage;
    @NotBlank
    private String typeExamen;
    @NotBlank
    private String resultats;
}