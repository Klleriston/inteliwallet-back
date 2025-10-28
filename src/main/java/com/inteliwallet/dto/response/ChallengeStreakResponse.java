package com.inteliwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeStreakResponse {

    private String id;
    private String participantId;
    private String challengeGoalId;
    private String challengeTitle;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastContributionDate;
    private Integer totalContributions;
    private Boolean streakActive;
    private Integer bonusPointsEarned;
    private Integer currentStreakBonus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}