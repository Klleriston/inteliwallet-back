package com.inteliwallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGoalRequest {

    private String title;

    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal targetAmount;

    private String category;

    private LocalDate deadline;
}