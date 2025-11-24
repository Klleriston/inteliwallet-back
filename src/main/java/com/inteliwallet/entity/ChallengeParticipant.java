package com.inteliwallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_participants",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_challenge_user", columnNames = {"challenge_goal_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_challenge_goal_id", columnList = "challenge_goal_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_goal_id", nullable = false)
    private ChallengeGoal challengeGoal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "contributed_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal contributedAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipantStatus status = ParticipantStatus.ACTIVE;

    @Column(name = "is_creator", nullable = false)
    private Boolean isCreator = false;

    @Column(name = "reward_claimed", nullable = false)
    private Boolean rewardClaimed = false;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ParticipantStatus {
        ACTIVE("active"),
        COMPLETED("completed"),
        LEFT("left");

        private final String value;

        ParticipantStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public void contribute(BigDecimal amount) {
        this.contributedAmount = this.contributedAmount.add(amount);
    }

    public BigDecimal getContributionPercentage(BigDecimal targetAmount) {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return contributedAmount
            .multiply(BigDecimal.valueOf(100))
            .divide(targetAmount, 2, java.math.RoundingMode.HALF_UP);
    }
}