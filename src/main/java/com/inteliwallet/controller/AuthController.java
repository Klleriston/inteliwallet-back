package com.inteliwallet.controller;

import com.inteliwallet.dto.request.LoginRequest;
import com.inteliwallet.dto.request.RegisterRequest;
import com.inteliwallet.dto.response.AuthResponse;
import com.inteliwallet.dto.response.UserResponse;
import com.inteliwallet.security.CurrentUser;
import com.inteliwallet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@CurrentUser String userId) {
        return ResponseEntity.ok(authService.getMe(userId));
    }
}
