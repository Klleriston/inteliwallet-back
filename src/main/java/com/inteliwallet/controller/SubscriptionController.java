package com.inteliwallet.controller;

import com.inteliwallet.dto.request.CreateSubscriptionRequest;
import com.inteliwallet.dto.response.PaymentResponse;
import com.inteliwallet.dto.response.SubscriptionResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createSubscription(
        @CurrentUser String userId,
        @Valid @RequestBody CreateSubscriptionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(subscriptionService.createSubscription(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getUserSubscriptions(@CurrentUser String userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<SubscriptionResponse> getActiveSubscription(@CurrentUser String userId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscription(userId));
    }

    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<Void> cancelSubscription(
        @CurrentUser String userId,
        @PathVariable String subscriptionId
    ) {
        subscriptionService.cancelSubscription(userId, subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(@CurrentUser String userId) {
        return ResponseEntity.ok(subscriptionService.getUserPayments(userId));
    }

    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
        @CurrentUser String userId,
        @PathVariable String paymentId
    ) {
        return ResponseEntity.ok(subscriptionService.getPayment(userId, paymentId));
    }

    @GetMapping("/webhook")
    public ResponseEntity<Map<String, String>> testWebhook() {
        log.info("Webhook GET endpoint acessado - teste do Mercado Pago");
        Map<String, String> response = new java.util.HashMap<>();
        response.put("status", "ok");
        response.put("message", "Webhook endpoint está acessível");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(
        @RequestBody(required = false) Map<String, Object> payload,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String id
    ) {
        log.info("Webhook recebido do Mercado Pago - Type: {}, ID: {}, Payload: {}", type, id, payload);

        try {
            if ("payment".equals(type) && id != null) {
                log.info("Processando notificação de pagamento: {}", id);

                String preferenceId = null;
                String status = null;

                if (payload.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) payload.get("data");
                    if (data != null && data.containsKey("id")) {
                        String paymentId = data.get("id").toString();
                        log.info("Payment ID extraído do data: {}", paymentId);
                    }
                }

                if (payload.containsKey("additional_info")) {
                    Map<String, Object> additionalInfo = (Map<String, Object>) payload.get("additional_info");
                    if (additionalInfo != null && additionalInfo.containsKey("external_reference")) {
                        preferenceId = additionalInfo.get("external_reference").toString();
                    }
                }

                if (payload.containsKey("external_reference")) {
                    preferenceId = payload.get("external_reference").toString();
                }

                if (payload.containsKey("status")) {
                    status = payload.get("status").toString();
                }

                if (preferenceId != null && status != null) {
                    log.info("Processando webhook - Preference ID: {}, Status: {}", preferenceId, status);
                    subscriptionService.processPaymentWebhook(preferenceId, status.toUpperCase());
                } else {
                    log.warn("Webhook sem preferenceId ou status. Ignorando...");
                }
            } else {
                log.info("Tipo de notificação não processado: {}", type);
            }

            Map<String, String> response = new java.util.HashMap<>();
            response.put("status", "ok");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao processar webhook do Mercado Pago", e);
            Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
