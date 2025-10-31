package com.inteliwallet.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoWebhookNotification {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("live_mode")
    private Boolean liveMode;

    @JsonProperty("type")
    private String type;

    @JsonProperty("date_created")
    private LocalDateTime dateCreated;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("api_version")
    private String apiVersion;

    @JsonProperty("action")
    private String action;

    @JsonProperty("data")
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        @JsonProperty("id")
        private String id;
    }
}