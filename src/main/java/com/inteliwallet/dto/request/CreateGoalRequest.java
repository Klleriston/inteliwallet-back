package com.inteliwallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoalRequest {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotNull(message = "Valor da meta é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    @NotNull(message = "Prazo é obrigatório")
    @Future(message = "Prazo deve ser no futuro")
    private LocalDate deadline;
}