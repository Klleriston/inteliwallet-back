package com.inteliwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String id;
    private String userId;
    private String subscriptionId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String paymentUrl;
    private String pixCode;
    private String pixQrCode;
    private LocalDateTime paidAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}