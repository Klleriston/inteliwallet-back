package com.inteliwallet.dto.request;

import com.inteliwallet.entity.UserPlan;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest {

    @NotNull(message = "Plano é obrigatório")
    private UserPlan plan;

    private String paymentMethod = "PIX";
}