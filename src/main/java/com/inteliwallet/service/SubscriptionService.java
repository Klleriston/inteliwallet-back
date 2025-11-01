package com.inteliwallet.service;

import com.inteliwallet.dto.request.CreateSubscriptionRequest;
import com.inteliwallet.dto.response.PaymentResponse;
import com.inteliwallet.dto.response.SubscriptionResponse;
import com.inteliwallet.entity.*;
import com.inteliwallet.entity.Payment.PaymentMethod;
import com.inteliwallet.entity.Payment.PaymentStatus;
import com.inteliwallet.entity.Subscription.SubscriptionStatus;
import com.inteliwallet.exception.BadRequestException;
import com.inteliwallet.exception.ResourceNotFoundException;
import com.inteliwallet.repository.PaymentRepository;
import com.inteliwallet.repository.SubscriptionRepository;
import com.inteliwallet.repository.UserRepository;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final MercadoPagoService mercadoPagoService;

    @Transactional
    public PaymentResponse createSubscription(String userId, CreateSubscriptionRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (request.getPlan() == UserPlan.FREE) {
            throw new BadRequestException("Não é necessário pagamento para o plano FREE");
        }

        if (user.getPlan() == request.getPlan()) {
            throw new BadRequestException("Você já possui este plano");
        }

        subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
            .ifPresent(sub -> {
                throw new BadRequestException("Você já possui uma assinatura ativa. Cancele antes de criar uma nova.");
            });

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(request.getPlan());
        subscription.setStatus(SubscriptionStatus.PENDING);
        subscription = subscriptionRepository.save(subscription);

        BigDecimal amount = BigDecimal.valueOf(request.getPlan().getMonthlyPrice());

        String title = "Assinatura " + request.getPlan().getDisplayName();
        String description = "Plano " + request.getPlan().getDisplayName() + " - InteliWallet";

        Preference preference = mercadoPagoService.createPaymentPreference(
            amount,
            title,
            description,
            subscription.getId(),
            user.getEmail(),
            user.getUsername()
        );

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setSubscription(subscription);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(PaymentMethod.PIX);
        payment.setExternalPaymentId(preference.getId());
        payment.setPaymentUrl(preference.getInitPoint());

        if (preference.getDateOfExpiration() != null) {
            payment.setExpiresAt(preference.getDateOfExpiration()
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime());
        }

        payment = paymentRepository.save(payment);

        return mapToPaymentResponse(payment);
    }


    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getUserSubscriptions(String userId) {
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream()
            .map(this::mapToSubscriptionResponse)
            .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public SubscriptionResponse getActiveSubscription(String userId) {
        Subscription subscription = subscriptionRepository
            .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Nenhuma assinatura ativa encontrada"));
        return mapToSubscriptionResponse(subscription);
    }

    @Transactional
    public void cancelSubscription(String userId, String subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada"));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new BadRequestException("Você não tem permissão para cancelar esta assinatura");
        }

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BadRequestException("Apenas assinaturas ativas podem ser canceladas");
        }

        subscription.setCancelAtPeriodEnd(true);
        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void processPaymentWebhook(String mercadoPagoPaymentId) {
        log.info("Processando webhook - Buscando pagamento no Mercado Pago. ID: {}", mercadoPagoPaymentId);

        // Busca os dados do pagamento na API do Mercado Pago
        com.mercadopago.resources.payment.Payment mpPayment;
        try {
            mpPayment = mercadoPagoService.getPayment(Long.parseLong(mercadoPagoPaymentId));
            log.info("Pagamento encontrado no Mercado Pago. Status: {}, External Reference: {}",
                mpPayment.getStatus(), mpPayment.getExternalReference());
        } catch (Exception e) {
            log.error("Erro ao buscar pagamento no Mercado Pago. ID: {}", mercadoPagoPaymentId, e);
            throw new RuntimeException("Erro ao buscar pagamento no Mercado Pago", e);
        }

        // Busca o pagamento local usando o external_reference (que é o subscription ID)
        String externalReference = mpPayment.getExternalReference();
        if (externalReference == null || externalReference.isEmpty()) {
            log.error("Pagamento do Mercado Pago sem external_reference. Payment ID: {}", mercadoPagoPaymentId);
            throw new ResourceNotFoundException("Pagamento sem referência externa");
        }

        // Busca a subscription pelo ID (que foi salvo como external_reference)
        Subscription subscription = subscriptionRepository.findById(externalReference)
            .orElseThrow(() -> {
                log.error("Assinatura não encontrada. External Reference: {}", externalReference);
                return new ResourceNotFoundException("Assinatura não encontrada");
            });

        // Busca o pagamento associado à subscription
        Payment payment = paymentRepository.findBySubscriptionId(subscription.getId())
            .stream()
            .filter(p -> p.getStatus() != PaymentStatus.PAID) // Pega o primeiro não pago
            .findFirst()
            .orElseThrow(() -> {
                log.error("Pagamento local não encontrado para subscription: {}", subscription.getId());
                return new ResourceNotFoundException("Pagamento não encontrado");
            });

        // Atualiza com o payment ID do Mercado Pago se ainda não tiver
        if (payment.getExternalPaymentId() == null || !payment.getExternalPaymentId().equals(mercadoPagoPaymentId)) {
            payment.setExternalPaymentId(mercadoPagoPaymentId);
            log.info("Payment ID do Mercado Pago atualizado: {}", mercadoPagoPaymentId);
        }

        // Mapeia o status do Mercado Pago para status interno
        String internalStatus = mercadoPagoService.mapMercadoPagoStatusToInternal(mpPayment.getStatus());
        log.info("Pagamento encontrado: ID={}, Status atual={}, Status MP={}, Status interno={}",
            payment.getId(), payment.getStatus(), mpPayment.getStatus(), internalStatus);

        // Processa o pagamento baseado no status
        if ("PAID".equals(internalStatus)) {
            payment.markAsPaid();
            paymentRepository.save(payment);
            log.info("Pagamento marcado como PAID: {}", payment.getId());

            log.info("Ativando assinatura: {}", subscription.getId());
            subscription.activate();
            subscriptionRepository.save(subscription);

            User user = subscription.getUser();
            UserPlan oldPlan = user.getPlan();
            user.setPlan(subscription.getPlan());
            userRepository.save(user);
            log.info("Plano do usuário {} atualizado de {} para {} - INSTANTÂNEO",
                user.getId(), oldPlan, subscription.getPlan());

        } else if ("FAILED".equals(internalStatus)) {
            payment.markAsFailed();
            paymentRepository.save(payment);
            log.info("Pagamento marcado como FAILED: {}", payment.getId());

            subscription.setStatus(SubscriptionStatus.CANCELED);
            subscriptionRepository.save(subscription);
            log.info("Assinatura cancelada devido a pagamento falho: {}", subscription.getId());

        } else {
            log.info("Pagamento com status pendente: {}. Aguardando confirmação.", internalStatus);
        }
    }

    public List<PaymentResponse> getUserPayments(String userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
            .map(this::mapToPaymentResponse)
            .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String userId, String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));

        if (!payment.getUser().getId().equals(userId)) {
            throw new BadRequestException("Você não tem permissão para visualizar este pagamento");
        }

        return mapToPaymentResponse(payment);
    }

    private SubscriptionResponse mapToSubscriptionResponse(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(subscription.getId());
        response.setUserId(subscription.getUser().getId());
        response.setPlan(subscription.getPlan().getValue());
        response.setStatus(subscription.getStatus().getValue());
        response.setCurrentPeriodStart(subscription.getCurrentPeriodStart());
        response.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
        response.setCancelAtPeriodEnd(subscription.getCancelAtPeriodEnd());
        response.setCreatedAt(subscription.getCreatedAt());
        return response;
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setUserId(payment.getUser().getId());
        response.setSubscriptionId(payment.getSubscription() != null ? payment.getSubscription().getId() : null);
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus().getValue());
        response.setPaymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().getValue() : null);
        response.setPaymentUrl(payment.getPaymentUrl());
        response.setPixCode(payment.getPixCode());
        response.setPixQrCode(payment.getPixQrCode());
        response.setPaidAt(payment.getPaidAt());
        response.setExpiresAt(payment.getExpiresAt());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}