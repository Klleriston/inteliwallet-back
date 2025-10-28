package com.inteliwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeParticipantResponse {

    private String id;
    private String challengeGoalId;
    private UserInfo user;
    private BigDecimal contributedAmount;
    private String status;
    private Boolean isCreator;
    private Boolean rewardClaimed;
    private BigDecimal contributionPercentage;
    private LocalDateTime joinedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String username;
        private String avatar;
        private Integer totalPoints;
    }
}