package com.inteliwallet.controller;

import com.inteliwallet.dto.response.ChallengeStreakResponse;
import com.inteliwallet.dto.response.UserStreakResponse;
import com.inteliwallet.entity.UserStreak.StreakType;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.StreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/streaks")
@RequiredArgsConstructor
public class StreakController {

    private final StreakService streakService;

    @GetMapping
    public ResponseEntity<List<UserStreakResponse>> getUserStreaks(@CurrentUser String userId) {
        return ResponseEntity.ok(streakService.getUserStreaks(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserStreakResponse>> getActiveUserStreaks(@CurrentUser String userId) {
        return ResponseEntity.ok(streakService.getActiveUserStreaks(userId));
    }


    @GetMapping("/type/{streakType}")
    public ResponseEntity<UserStreakResponse> getUserStreakByType(
        @CurrentUser String userId,
        @PathVariable StreakType streakType
    ) {
        return ResponseEntity.ok(streakService.getUserStreakByType(userId, streakType));
    }

    @PostMapping("/record/{streakType}")
    public ResponseEntity<UserStreakResponse> recordActivity(
        @CurrentUser String userId,
        @PathVariable StreakType streakType
    ) {
        return ResponseEntity.ok(streakService.recordActivity(userId, streakType));
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<ChallengeStreakResponse>> getUserChallengeStreaks(@CurrentUser String userId) {
        return ResponseEntity.ok(streakService.getUserChallengeStreaks(userId));
    }

    @GetMapping("/challenges/{challengeGoalId}")
    public ResponseEntity<List<ChallengeStreakResponse>> getChallengeStreaks(@PathVariable String challengeGoalId) {
        return ResponseEntity.ok(streakService.getChallengeStreaks(challengeGoalId));
    }

    @GetMapping("/challenges/participant/{participantId}")
    public ResponseEntity<ChallengeStreakResponse> getChallengeStreakByParticipant(@PathVariable String participantId) {
        return ResponseEntity.ok(streakService.getChallengeStreakByParticipant(participantId));
    }
}
