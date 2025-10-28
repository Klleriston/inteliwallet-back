package com.inteliwallet.repository;

import com.inteliwallet.entity.Payment;
import com.inteliwallet.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByUserId(String userId);

    List<Payment> findByUserIdAndStatus(String userId, PaymentStatus status);

    List<Payment> findBySubscriptionId(String subscriptionId);

    Optional<Payment> findByExternalPaymentId(String externalPaymentId);

    List<Payment> findByStatus(PaymentStatus status);
}