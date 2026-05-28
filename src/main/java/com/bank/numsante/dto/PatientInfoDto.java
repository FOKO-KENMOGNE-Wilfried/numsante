package com.bank.numsante.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data @AllArgsConstructor
public class PatientInfoDto {
    private UUID idPatient;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String groupeSanguin;
}