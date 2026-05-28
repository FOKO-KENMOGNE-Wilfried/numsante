package com.bank.numsante.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QrScanRequest {
    @NotBlank
    private String qrCodeToken;
    @NotNull
    private Long idHopital;
}