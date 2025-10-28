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
@Table(name = "challenge_streaks",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_participant_streak", columnNames = {"participant_id"})
    },
    indexes = {
        @Index(name = "idx_participant_id", columnList = "participant_id"),
        @Index(name = "idx_challenge_goal_id", columnList = "challenge_goal_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeStreak {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private ChallengeParticipant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_goal_id", nullable = false)
    private ChallengeGoal challengeGoal;

    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak = 0;

    @Column(name = "last_contribution_date")
    private LocalDate lastContributionDate;

    @Column(name = "total_contributions", nullable = false)
    private Integer totalContributions = 0;

    @Column(name = "streak_active", nullable = false)
    private Boolean streakActive = true;

    @Column(name = "bonus_points_earned", nullable = false)
    private Integer bonusPointsEarned = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void recordContribution() {
        LocalDate today = LocalDate.now();

        if (lastContributionDate == null) {
            this.currentStreak = 1;
            this.totalContributions = 1;
        } else {
            long daysSinceLastContribution = java.time.temporal.ChronoUnit.DAYS
                .between(lastContributionDate, today);

            if (daysSinceLastContribution == 1) {
                this.currentStreak++;
                this.totalContributions++;
            } else if (daysSinceLastContribution == 0) {
                this.totalContributions++;
            } else {
                this.currentStreak = 1;
                this.totalContributions++;
            }
        }

        if (this.currentStreak > this.longestStreak) {
            this.longestStreak = this.currentStreak;
        }

        this.lastContributionDate = today;
        this.streakActive = true;

        if (this.currentStreak % 7 == 0) {
            this.bonusPointsEarned += 50;
        }
    }

    public void checkAndUpdateStreak(LocalDate today) {
        if (lastContributionDate == null) {
            return;
        }

        long daysSinceLastContribution = java.time.temporal.ChronoUnit.DAYS
            .between(lastContributionDate, today);

        if (daysSinceLastContribution > 1) {
            this.streakActive = false;
        }
    }

    public Integer calculateStreakBonus() {
        if (currentStreak >= 30) return 200;
        if (currentStreak >= 14) return 100;
        if (currentStreak >= 7) return 50;
        if (currentStreak >= 3) return 20;
        return 0;
    }
}