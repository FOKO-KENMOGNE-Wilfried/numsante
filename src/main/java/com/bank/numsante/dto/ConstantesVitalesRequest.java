package com.bank.numsante.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class ConstantesVitalesRequest {
    @NotNull
    private Map<String, Object> constantesVitales;
}