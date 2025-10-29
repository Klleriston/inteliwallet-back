package com.inteliwallet.controller;

import com.inteliwallet.dto.response.AchievementResponse;
import com.inteliwallet.dto.response.ChallengeGoalResponse;
import com.inteliwallet.dto.response.LeaderboardResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.AchievementService;
import com.inteliwallet.service.ChallengeGoalService;
import com.inteliwallet.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final AchievementService achievementService;
    private final ChallengeGoalService challengeGoalService;
    private final LeaderboardService leaderboardService;

    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementResponse>> getAchievements(@CurrentUser String userId) {
        return ResponseEntity.ok(achievementService.getAllAchievements(userId));
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<ChallengeGoalResponse>> getChallenges(@CurrentUser String userId) {
        return ResponseEntity.ok(challengeGoalService.listMyChallenges(userId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard(
        @CurrentUser String userId,
        @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(userId, limit));
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getGamificationOverview(@CurrentUser String userId) {
        Map<String, Object> overview = new HashMap<>();

        List<AchievementResponse> achievements = achievementService.getAllAchievements(userId);
        long unlockedCount = achievements.stream().filter(AchievementResponse::getUnlocked).count();

        List<ChallengeGoalResponse> challenges = challengeGoalService.listActiveChallenges(userId);

        List<LeaderboardResponse> leaderboard = leaderboardService.getGlobalLeaderboard(userId, 10);
        LeaderboardResponse currentUserRank = leaderboard.stream()
            .filter(LeaderboardResponse::getIsCurrentUser)
            .findFirst()
            .orElse(null);

        overview.put("totalAchievements", achievements.size());
        overview.put("unlockedAchievements", unlockedCount);
        overview.put("activeChallenges", challenges.size());
        overview.put("userRank", currentUserRank != null ? currentUserRank.getRank() : null);
        overview.put("userPoints", currentUserRank != null ? currentUserRank.getTotalPoints() : 0);

        return ResponseEntity.ok(overview);
    }
}
