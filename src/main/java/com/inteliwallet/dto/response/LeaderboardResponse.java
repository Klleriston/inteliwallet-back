package com.inteliwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {
    private Integer rank;
    private String userId;
    private String username;
    private String avatar;
    private Integer totalPoints;
    private Integer level;
    private Integer achievementsUnlocked;
    private Boolean isCurrentUser;
}