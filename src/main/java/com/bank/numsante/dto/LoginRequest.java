package com.bank.numsante.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String identifiantPro;
    @NotBlank
    private String motDePasse;
}