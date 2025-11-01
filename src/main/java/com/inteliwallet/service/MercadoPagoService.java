package com.inteliwallet.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;

@Slf4j
@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.notification.url}")
    private String notificationUrl;

    @Value("${mercadopago.success.url}")
    private String successUrl;

    @Value("${mercadopago.failure.url}")
    private String failureUrl;

    public MercadoPagoService(@Value("${mercadopago.access.token}") String accessToken) {
        this.accessToken = accessToken;
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    @Transactional
    public Preference createPaymentPreference(
        BigDecimal amount,
        String title,
        String description,
        String externalReference,
        String payerEmail,
        String payerName
    ) {
        try {
            log.info("Criando preferência de pagamento no Mercado Pago para: {}", title);
            log.debug("Success URL: {}, Failure URL: {}, Notification URL: {}",
                successUrl, failureUrl, notificationUrl);

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title(title)
                .description(description)
                .quantity(1)
                .currencyId("BRL")
                .unitPrice(amount)
                .build();

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl)
                .failure(failureUrl)
                .pending(failureUrl)
                .build();

            PreferenceRequest.PreferenceRequestBuilder requestBuilder = PreferenceRequest.builder()
                .items(Collections.singletonList(itemRequest))
                .backUrls(backUrls)
                .autoReturn("approved")  // Redireciona automaticamente após pagamento aprovado
                .notificationUrl(notificationUrl)
                .externalReference(externalReference)
                .statementDescriptor("InteliWallet");

            if (payerEmail != null && !payerEmail.isEmpty()) {
                PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(payerEmail)
                    .name(payerName)
                    .build();
                requestBuilder.payer(payer);
            }

            PreferenceRequest preferenceRequest = requestBuilder.build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            log.info("Preferência criada com sucesso. ID: {}", preference.getId());
            return preference;

        } catch (MPApiException apiException) {
            log.error("Erro de API do Mercado Pago: Status={}, Content={}",
                apiException.getStatusCode(), apiException.getApiResponse().getContent());
            throw new RuntimeException("Erro ao criar pagamento no Mercado Pago: " +
                apiException.getApiResponse().getContent(), apiException);
        } catch (MPException exception) {
            log.error("Erro ao comunicar com Mercado Pago", exception);
            throw new RuntimeException("Erro ao processar pagamento", exception);
        }
    }

    @Transactional(readOnly = true)
    public Payment getPayment(Long paymentId) {
        try {
            log.info("Consultando pagamento no Mercado Pago. ID: {}", paymentId);

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            log.info("Pagamento consultado. Status: {}", payment.getStatus());
            return payment;

        } catch (MPApiException apiException) {
            log.error("Erro de API ao consultar pagamento: Status={}, Content={}",
                apiException.getStatusCode(), apiException.getApiResponse().getContent());
            throw new RuntimeException("Erro ao consultar pagamento: " +
                apiException.getApiResponse().getContent(), apiException);
        } catch (MPException exception) {
            log.error("Erro ao consultar pagamento no Mercado Pago", exception);
            throw new RuntimeException("Erro ao consultar pagamento", exception);
        }
    }

    public String mapMercadoPagoStatusToInternal(String mpStatus) {
        return switch (mpStatus) {
            case "approved" -> "PAID";
            case "pending", "in_process", "in_mediation" -> "PENDING";
            case "rejected", "cancelled" -> "FAILED";
            case "refunded", "charged_back" -> "REFUNDED";
            default -> {
                log.warn("Status desconhecido do Mercado Pago: {}", mpStatus);
                yield "PENDING";
            }
        };
    }
}
