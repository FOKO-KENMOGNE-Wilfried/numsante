package com.bank.numsante.dto;

import lombok.Data;

@Data
public class UpdatePersonnelRequest {
    private String nom;
    private String prenom;
    private String role;
    private Long idHopital;
    private Boolean estActif;
}