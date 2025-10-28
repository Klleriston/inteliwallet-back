package com.inteliwallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_streaks",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_streak_type", columnNames = {"user_id", "streak_type"})
    },
    indexes = {
        @Index(name = "idx_user_streak", columnList = "user_id,streak_type")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "streak_type", nullable = false, length = 50)
    private StreakType streakType;

    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(name = "total_days_active", nullable = false)
    private Integer totalDaysActive = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum StreakType {
        DAILY_LOGIN("daily_login", "Login Diário"),
        MONTHLY_SAVINGS("monthly_savings", "Economia Mensal"),
        TRANSACTION_LOG("transaction_log", "Registro de Transações"),
        CHALLENGE_PARTICIPATION("challenge_participation", "Participação em Desafios"),
        GOAL_PROGRESS("goal_progress", "Progresso de Metas");

        private final String value;
        private final String displayName;

        StreakType(String value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        public String getValue() {
            return value;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public void incrementStreak() {
        this.currentStreak++;
        this.totalDaysActive++;
        if (this.currentStreak > this.longestStreak) {
            this.longestStreak = this.currentStreak;
        }
        this.lastActivityDate = LocalDate.now();
        this.isActive = true;
    }

    public void resetStreak() {
        this.currentStreak = 0;
        this.isActive = false;
    }

    public void checkAndUpdateStreak(LocalDate today) {
        if (lastActivityDate == null) {
            return;
        }

        long daysSinceLastActivity = java.time.temporal.ChronoUnit.DAYS.between(lastActivityDate, today);

        if (daysSinceLastActivity > 1) {
            resetStreak();
        }
    }
}
