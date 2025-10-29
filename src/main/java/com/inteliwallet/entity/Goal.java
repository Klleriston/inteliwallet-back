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

@Entity
@Table(name = "goals", indexes = {
    @Index(name = "idx_user_status", columnList = "user_id,status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "target_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status = GoalStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum GoalStatus {
        ACTIVE("active"),
        COMPLETED("completed"),
        OVERDUE("overdue");

        private final String value;

        GoalStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public void contribute(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);

        if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
            this.status = GoalStatus.COMPLETED;
        }
    }

    public void checkOverdue() {
        if (this.status == GoalStatus.ACTIVE &&
            LocalDate.now().isAfter(this.deadline)) {
            this.status = GoalStatus.OVERDUE;
        }
    }

    public BigDecimal getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount.divide(targetAmount, 2, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }
}