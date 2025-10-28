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

    @GetMapping("/webhook/test")
    public ResponseEntity<Map<String, String>> testWebhook() {
        log.info("Endpoint de teste do webhook acessado");
        Map<String, String> response = new java.util.HashMap<>();
        response.put("status", "ok");
        response.put("message", "Webhook endpoint está acessível");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Webhook recebido do AbacatePay: {}", payload);

        String paymentId = (String) payload.get("id");
        if (paymentId == null) {
            paymentId = (String) payload.get("paymentId");
        }

        String status = (String) payload.get("status");

        log.info("Processando pagamento - ID: {}, Status: {}", paymentId, status);

        if (paymentId == null || status == null) {
            log.error("Webhook inválido - paymentId ou status ausente. Payload: {}", payload);
            return ResponseEntity.badRequest().build();
        }

        subscriptionService.processPaymentWebhook(paymentId, status);
        return ResponseEntity.ok().build();
    }
}
