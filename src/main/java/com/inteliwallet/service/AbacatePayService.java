package com.inteliwallet.service;

import com.inteliwallet.dto.external.AbacatePayBillingRequest;
import com.inteliwallet.dto.external.AbacatePayBillingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AbacatePayService {

    @Value("${abacatepay.api.url:https://api.abacatepay.com}")
    private String apiUrl;

    @Value("${abacatepay.api.key}")
    private String apiKey;

    @Value("${abacatepay.return.url:http://localhost:3000/payment/success}")
    private String returnUrl;

    @Value("${abacatepay.completion.url:http://localhost:3000/payment/complete}")
    private String completionUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public AbacatePayBillingResponse createBilling(
        BigDecimal amount,
        String customerEmail,
        String customerName,
        String customerPhone,
        String metadata
    ) {
        try {
            String url = apiUrl + "/v1/billing/create";

            BigDecimal amountInCents = amount.multiply(BigDecimal.valueOf(100));

            AbacatePayBillingRequest.CustomerInfo customer = null;
            if (customerPhone != null && !customerPhone.isEmpty()) {
                customer = new AbacatePayBillingRequest.CustomerInfo();
                customer.setEmail(customerEmail);
                customer.setName(customerName);
                customer.setCellphone(customerPhone);
            }

            AbacatePayBillingRequest.ProductInfo product = new AbacatePayBillingRequest.ProductInfo();
            product.setExternalId("subscription");
            product.setName(metadata);
            product.setDescription(metadata);
            product.setQuantity(1);
            product.setPrice(amountInCents);

            AbacatePayBillingRequest request = new AbacatePayBillingRequest();
            request.setAmount(amountInCents);
            request.setFrequency("ONE_TIME");
            request.setMethods(List.of("PIX"));
            request.setCustomer(customer);
            request.setMetadata(metadata);
            request.setProducts(List.of(product));
            request.setReturnUrl(returnUrl);
            request.setCompletionUrl(completionUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<AbacatePayBillingRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AbacatePayBillingResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                AbacatePayBillingResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Erro ao criar cobrança no AbacatePay", e);
            throw new RuntimeException("Erro ao processar pagamento", e);
        }
    }

    public AbacatePayBillingResponse getBilling(String billingId) {
        try {
            String url = apiUrl + "/billing/get?id=" + billingId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AbacatePayBillingResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                AbacatePayBillingResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Erro ao consultar cobrança no AbacatePay", e);
            throw new RuntimeException("Erro ao consultar pagamento", e);
        }
    }
}