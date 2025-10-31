package com.inteliwallet.service;

import com.inteliwallet.dto.request.LoginRequest;
import com.inteliwallet.dto.request.RegisterRequest;
import com.inteliwallet.dto.response.AuthResponse;
import com.inteliwallet.dto.response.UserResponse;
import com.inteliwallet.entity.User;
import com.inteliwallet.exception.BadRequestException;
import com.inteliwallet.repository.UserRepository;
import com.inteliwallet.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final StreakService streakService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Dado invalido");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Dado invalido");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAvatar("👤");
        user.setTotalPoints(0);
        user.setLevel(1);
        user.setHasCompletedOnboarding(false);

        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user.getId());

        return new AuthResponse(token, mapToUserResponse(user));
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Credenciais inválidas"));

            try {
                streakService.recordDailyLogin(user.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            String token = tokenProvider.generateToken(user.getId());

            return new AuthResponse(token, mapToUserResponse(user));

        } catch (AuthenticationException e) {
            throw new BadRequestException("Credenciais inválidas");
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = modelMapper.map(user, UserResponse.class);
        if (response.getHasCompletedOnboarding() == null) {
            response.setHasCompletedOnboarding(false);
        }
        response.setPlan(user.getPlan().getValue());
        response.setPlanDisplayName(user.getPlan().getDisplayName());
        return response;
    }
}
