package com.inteliwallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributeChallengeRequest {

    @NotNull(message = "Valor da contribuição é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor da contribuição deve ser maior que zero")
    private BigDecimal amount;

    private String note;
}