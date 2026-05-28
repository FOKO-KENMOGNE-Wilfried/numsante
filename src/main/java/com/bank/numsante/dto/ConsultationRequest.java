package com.bank.numsante.dto;
import lombok.Data;

@Data
public class ConsultationRequest {
    private String diagnostic;
    private String prescriptionOrdonnance;
    private boolean cloturerPassage;
}