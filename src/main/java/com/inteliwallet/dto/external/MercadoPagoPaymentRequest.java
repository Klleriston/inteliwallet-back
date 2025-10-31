package com.inteliwallet.dto.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MercadoPagoPaymentRequest {

    @JsonProperty("items")
    private List<Item> items;

    @JsonProperty("payer")
    private Payer payer;

    @JsonProperty("back_urls")
    private BackUrls backUrls;

    @JsonProperty("notification_url")
    private String notificationUrl;

    @JsonProperty("auto_return")
    private String autoReturn = "approved";

    @JsonProperty("payment_methods")
    private PaymentMethods paymentMethods;

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("statement_descriptor")
    private String statementDescriptor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {
        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("quantity")
        private Integer quantity;

        @JsonProperty("unit_price")
        private BigDecimal unitPrice;

        @JsonProperty("currency_id")
        private String currencyId = "BRL";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payer {
        @JsonProperty("name")
        private String name;

        @JsonProperty("surname")
        private String surname;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private Phone phone;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Phone {
            @JsonProperty("area_code")
            private String areaCode;

            @JsonProperty("number")
            private String number;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BackUrls {
        @JsonProperty("success")
        private String success;

        @JsonProperty("failure")
        private String failure;

        @JsonProperty("pending")
        private String pending;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaymentMethods {
        @JsonProperty("excluded_payment_types")
        private List<ExcludedPaymentType> excludedPaymentTypes;

        @JsonProperty("installments")
        private Integer installments;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class ExcludedPaymentType {
            @JsonProperty("id")
            private String id;
        }
    }
}
