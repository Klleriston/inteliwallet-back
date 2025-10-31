package com.inteliwallet.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoPaymentResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("init_point")
    private String initPoint;

    @JsonProperty("sandbox_init_point")
    private String sandboxInitPoint;

    @JsonProperty("date_created")
    private LocalDateTime dateCreated;

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("collector_id")
    private Long collectorId;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("items")
    private Object items;

    @JsonProperty("payer")
    private Object payer;

    @JsonProperty("back_urls")
    private Object backUrls;

    @JsonProperty("auto_return")
    private String autoReturn;

    @JsonProperty("payment_methods")
    private Object paymentMethods;

    @JsonProperty("notification_url")
    private String notificationUrl;

    @JsonProperty("date_of_expiration")
    private LocalDateTime dateOfExpiration;

    @JsonProperty("expiration_date_from")
    private LocalDateTime expirationDateFrom;

    @JsonProperty("expiration_date_to")
    private LocalDateTime expirationDateTo;
}
