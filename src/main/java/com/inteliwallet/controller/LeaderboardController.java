package com.inteliwallet.controller;

import com.inteliwallet.dto.response.LeaderboardResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardResponse>> getGlobalLeaderboard(
        @CurrentUser String userId,
        @RequestParam(required = false, defaultValue = "50") Integer limit
    ) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(userId, limit));
    }

    @GetMapping("/friends")
    public ResponseEntity<List<LeaderboardResponse>> getFriendsLeaderboard(@CurrentUser String userId) {
        return ResponseEntity.ok(leaderboardService.getFriendsLeaderboard(userId));
    }
}
