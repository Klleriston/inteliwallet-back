package com.inteliwallet.controller;

import com.inteliwallet.dto.request.ContributeChallengeRequest;
import com.inteliwallet.dto.request.CreateChallengeGoalRequest;
import com.inteliwallet.dto.request.UpdateChallengeGoalRequest;
import com.inteliwallet.dto.response.ChallengeGoalResponse;
import com.inteliwallet.dto.response.ChallengeParticipantResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.ChallengeGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeGoalController {

    private final ChallengeGoalService challengeGoalService;

    @PostMapping
    public ResponseEntity<ChallengeGoalResponse> createChallenge(
        @CurrentUser String userId,
        @Valid @RequestBody CreateChallengeGoalRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(challengeGoalService.createChallenge(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<ChallengeGoalResponse>> listMyChallenges(@CurrentUser String userId) {
        return ResponseEntity.ok(challengeGoalService.listMyChallenges(userId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ChallengeGoalResponse>> listActiveChallenges(@CurrentUser String userId) {
        return ResponseEntity.ok(challengeGoalService.listActiveChallenges(userId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ChallengeGoalResponse>> listAvailableChallenges() {
        return ResponseEntity.ok(challengeGoalService.listAvailableChallenges());
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeGoalResponse> getChallengeById(@PathVariable String challengeId) {
        return ResponseEntity.ok(challengeGoalService.getChallengeById(challengeId));
    }

    @PutMapping("/{challengeId}")
    public ResponseEntity<ChallengeGoalResponse> updateChallenge(
        @CurrentUser String userId,
        @PathVariable String challengeId,
        @Valid @RequestBody UpdateChallengeGoalRequest request
    ) {
        return ResponseEntity.ok(challengeGoalService.updateChallenge(userId, challengeId, request));
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(
        @CurrentUser String userId,
        @PathVariable String challengeId
    ) {
        challengeGoalService.deleteChallenge(userId, challengeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{challengeId}/join")
    public ResponseEntity<ChallengeParticipantResponse> joinChallenge(
        @CurrentUser String userId,
        @PathVariable String challengeId
    ) {
        return ResponseEntity.ok(challengeGoalService.joinChallenge(userId, challengeId));
    }

    @PostMapping("/{challengeId}/leave")
    public ResponseEntity<Void> leaveChallenge(
        @CurrentUser String userId,
        @PathVariable String challengeId
    ) {
        challengeGoalService.leaveChallenge(userId, challengeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{challengeId}/contribute")
    public ResponseEntity<ChallengeGoalResponse> contributeToChallenge(
        @CurrentUser String userId,
        @PathVariable String challengeId,
        @Valid @RequestBody ContributeChallengeRequest request
    ) {
        return ResponseEntity.ok(challengeGoalService.contributeToChallenge(userId, challengeId, request));
    }

    @GetMapping("/{challengeId}/participants")
    public ResponseEntity<List<ChallengeParticipantResponse>> listParticipants(@PathVariable String challengeId) {
        return ResponseEntity.ok(challengeGoalService.listParticipants(challengeId));
    }
}