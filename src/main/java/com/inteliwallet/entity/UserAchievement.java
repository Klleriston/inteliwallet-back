package com.inteliwallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
}, indexes = {
    @Index(name = "idx_user_achievement", columnList = "user_id,achievement_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    @Column(name = "current_progress", nullable = false)
    private Integer currentProgress = 0;

    @Column(nullable = false)
    private Boolean unlocked = false;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void incrementProgress() {
        this.currentProgress++;
    }

    public void setProgress(Integer progress) {
        this.currentProgress = progress;
    }

    public void unlock() {
        this.unlocked = true;
        this.unlockedAt = LocalDateTime.now();
    }

    public Integer getProgressPercentage() {
        if (achievement.getTargetValue() == null || achievement.getTargetValue() == 0) {
            return unlocked ? 100 : 0;
        }
        int percentage = (currentProgress * 100) / achievement.getTargetValue();
        return Math.min(percentage, 100);
    }
}