package com.bank.numsante.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class DashboardStats {
    private long totalPatients;
    private long totalHopitaux;
    private long totalPersonnel;
    private long totalPassagesAujourdhui;
    private long totalPassagesEnCours;
    private long totalExamensRealises;
    private long totalOrdonnances;
}