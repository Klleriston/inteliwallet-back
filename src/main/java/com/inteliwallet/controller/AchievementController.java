package com.inteliwallet.controller;

import com.inteliwallet.dto.response.AchievementResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<List<AchievementResponse>> getAllAchievements(@CurrentUser String userId) {
        return ResponseEntity.ok(achievementService.getAllAchievements(userId));
    }

    @GetMapping("/unlocked")
    public ResponseEntity<List<AchievementResponse>> getUnlockedAchievements(@CurrentUser String userId) {
        return ResponseEntity.ok(achievementService.getUserUnlockedAchievements(userId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<AchievementResponse>> getAchievementsByCategory(
        @CurrentUser String userId,
        @PathVariable String category
    ) {
        return ResponseEntity.ok(achievementService.getAchievementsByCategory(userId, category));
    }

    @PostMapping("/initialize")
    public ResponseEntity<Void> initializeUserAchievements(@CurrentUser String userId) {
        achievementService.initializeUserAchievements(userId);
        return ResponseEntity.ok().build();
    }
}
