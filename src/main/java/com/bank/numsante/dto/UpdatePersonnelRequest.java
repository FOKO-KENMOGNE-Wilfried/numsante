package com.bank.numsante.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePersonnelRequest {
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    private String role;
    private Long idHopital;
    private Boolean estActif;
}