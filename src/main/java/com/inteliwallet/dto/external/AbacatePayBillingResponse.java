package com.inteliwallet.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbacatePayBillingResponse {

    private BillingData data;
    private String error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingData {
        private String id;
        private String url;
        private BigDecimal amount;
        private String status;
        private List<String> methods;
        private PixInfo pix;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PixInfo {
        private String qrCode;
        private String qrCodeBase64;
        private String pixKey;
    }
}