package com.inteliwallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "challenge_goals", indexes = {
    @Index(name = "idx_creator_id", columnList = "creator_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_deadline", columnList = "deadline")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChallengeStatus status = ChallengeStatus.ACTIVE;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "reward_points")
    private Integer rewardPoints = 0;

    @OneToMany(mappedBy = "challengeGoal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeParticipant> participants;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ChallengeStatus {
        ACTIVE("active"),
        COMPLETED("completed"),
        FAILED("failed"),
        CANCELLED("cancelled");

        private final String value;

        ChallengeStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public void contribute(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
            this.status = ChallengeStatus.COMPLETED;
        }
    }

    public BigDecimal getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount
            .multiply(BigDecimal.valueOf(100))
            .divide(targetAmount, 2, java.math.RoundingMode.HALF_UP);
    }

    public void checkOverdue() {
        if (status == ChallengeStatus.ACTIVE && deadline.isBefore(LocalDate.now())) {
            status = ChallengeStatus.FAILED;
        }
    }

    public boolean isFull() {
        return maxParticipants != null && participants != null &&
               participants.size() >= maxParticipants;
    }

    public int getParticipantCount() {
        return participants != null ? participants.size() : 0;
    }
}
