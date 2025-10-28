package com.inteliwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeGoalResponse {

    private String id;
    private CreatorInfo creator;
    private String title;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private String category;
    private LocalDate deadline;
    private String status;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer rewardPoints;
    private BigDecimal progressPercentage;
    private List<ParticipantInfo> topContributors;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatorInfo {
        private String id;
        private String username;
        private String avatar;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantInfo {
        private String id;
        private String userId;
        private String username;
        private String avatar;
        private BigDecimal contributedAmount;
        private BigDecimal contributionPercentage;
        private Boolean isCreator;
    }
}