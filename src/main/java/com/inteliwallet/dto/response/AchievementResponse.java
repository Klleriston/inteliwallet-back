package com.inteliwallet.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponse {
    private String id;
    private String title;
    private String description;
    private String icon;
    private Integer points;
    private String code;
    private String category;
    private Integer targetValue;
    private Integer currentProgress;
    private Integer progressPercentage;
    private Boolean unlocked;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime unlockedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}