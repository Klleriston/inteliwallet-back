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
    public void processPaymentWebhook(String paymentId, String status) {
        log.info("Processando webhook - Buscando pagamento com externalPaymentId: {}", paymentId);

        Payment payment = paymentRepository.findByExternalPaymentId(paymentId)
            .orElseThrow(() -> {
                log.error("Pagamento não encontrado com externalPaymentId: {}", paymentId);
                return new ResourceNotFoundException("Pagamento não encontrado");
            });

        log.info("Pagamento encontrado: ID={}, Status atual={}, Novo status={}",
            payment.getId(), payment.getStatus(), status);

        if ("PAID".equalsIgnoreCase(status) || "paid".equalsIgnoreCase(status)) {
            payment.markAsPaid();
            paymentRepository.save(payment);
            log.info("Pagamento marcado como PAID: {}", payment.getId());

            Subscription subscription = payment.getSubscription();
            if (subscription != null) {
                log.info("Ativando assinatura: {}", subscription.getId());
                subscription.activate();
                subscriptionRepository.save(subscription);

                User user = subscription.getUser();
                UserPlan oldPlan = user.getPlan();
                user.setPlan(subscription.getPlan());
                userRepository.save(user);
                log.info("Plano do usuário {} atualizado de {} para {}",
                    user.getId(), oldPlan, subscription.getPlan());
            } else {
                log.warn("Pagamento {} não possui assinatura associada", payment.getId());
            }
        } else if ("FAILED".equalsIgnoreCase(status) || "failed".equalsIgnoreCase(status)) {
            payment.markAsFailed();
            paymentRepository.save(payment);
            log.info("Pagamento marcado como FAILED: {}", payment.getId());
        } else {
            log.warn("Status desconhecido recebido no webhook: {}", status);
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