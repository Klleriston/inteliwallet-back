package com.inteliwallet.dto.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbacatePayBillingRequest {

    private BigDecimal amount;
    private String frequency;
    private List<String> methods;
    private CustomerInfo customer;
    private String metadata;
    private List<ProductInfo> products;
    private String returnUrl;
    private String completionUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomerInfo {
        private String email;
        private String name;
        private String cellphone;
        private String Id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private String externalId;
        private String name;
        private String description;
        private Integer quantity;
        private BigDecimal price;
    }
}
