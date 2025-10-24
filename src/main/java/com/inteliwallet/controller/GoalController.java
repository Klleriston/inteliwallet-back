package com.inteliwallet.controller;

import com.inteliwallet.dto.request.ContributeGoalRequest;
import com.inteliwallet.dto.request.CreateGoalRequest;
import com.inteliwallet.dto.request.UpdateGoalRequest;
import com.inteliwallet.dto.response.GoalResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalResponse>> listGoals(@CurrentUser String userId) {
        return ResponseEntity.ok(goalService.listGoals(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(
        @CurrentUser String userId,
        @PathVariable String id
    ) {
        return ResponseEntity.ok(goalService.getGoal(userId, id));
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
        @CurrentUser String userId,
        @Valid @RequestBody CreateGoalRequest request
    ) {
        return ResponseEntity.ok(goalService.createGoal(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(
        @CurrentUser String userId,
        @PathVariable String id,
        @Valid @RequestBody UpdateGoalRequest request
    ) {
        return ResponseEntity.ok(goalService.updateGoal(userId, id, request));
    }

    @PostMapping("/{id}/contribute")
    public ResponseEntity<GoalResponse> contributeToGoal(
        @CurrentUser String userId,
        @PathVariable String id,
        @Valid @RequestBody ContributeGoalRequest request
    ) {
        return ResponseEntity.ok(goalService.contributeToGoal(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
        @CurrentUser String userId,
        @PathVariable String id
    ) {
        goalService.deleteGoal(userId, id);
        return ResponseEntity.noContent().build();
    }
}