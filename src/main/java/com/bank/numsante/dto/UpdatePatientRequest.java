package com.bank.numsante.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdatePatientRequest {
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String telephone;
    private String groupeSanguin;
}