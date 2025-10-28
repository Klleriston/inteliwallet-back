package com.inteliwallet.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChallengeGoalRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    private String title;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String description;

    @NotNull(message = "Valor alvo é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor alvo deve ser maior que zero")
    private BigDecimal targetAmount;

    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    private String category;

    @NotNull(message = "Data limite é obrigatória")
    @Future(message = "Data limite deve ser no futuro")
    private LocalDate deadline;

    @Min(value = 2, message = "Deve ter no mínimo 2 participantes")
    @Max(value = 50, message = "Máximo de 50 participantes")
    private Integer maxParticipants;

    @Min(value = 0, message = "Pontos de recompensa devem ser positivos")
    private Integer rewardPoints = 100;
}
