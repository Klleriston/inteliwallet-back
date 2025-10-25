package com.inteliwallet.controller;

import com.inteliwallet.dto.request.ChangePasswordRequest;
import com.inteliwallet.dto.request.UpdateUserRequest;
import com.inteliwallet.dto.response.UserResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@CurrentUser String userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
        @CurrentUser String userId,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteAccount(@CurrentUser String userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
        @CurrentUser String userId,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }
}