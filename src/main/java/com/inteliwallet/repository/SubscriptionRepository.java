package com.inteliwallet.repository;

import com.inteliwallet.entity.Subscription;
import com.inteliwallet.entity.Subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findByUserId(String userId);

    Optional<Subscription> findByUserIdAndStatus(String userId, SubscriptionStatus status);

    Optional<Subscription> findByExternalSubscriptionId(String externalSubscriptionId);

    List<Subscription> findByStatus(SubscriptionStatus status);
}
