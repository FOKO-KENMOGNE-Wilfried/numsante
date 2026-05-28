package com.bank.numsante.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EnregistrementPatientRequest {
    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotNull
    private LocalDate dateNaissance;

    @NotNull
    private Character genre; // 'M' ou 'F'

    private String groupeSanguin;
    private String telephone;

    @NotNull
    private Long idHopital; // Hôpital où le patient est enregistré
}