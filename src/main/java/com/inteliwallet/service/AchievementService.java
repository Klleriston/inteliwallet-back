package com.inteliwallet.service;

import com.inteliwallet.dto.response.AchievementResponse;
import com.inteliwallet.entity.Achievement;
import com.inteliwallet.entity.User;
import com.inteliwallet.entity.UserAchievement;
import com.inteliwallet.exception.ResourceNotFoundException;
import com.inteliwallet.repository.AchievementRepository;
import com.inteliwallet.repository.UserAchievementRepository;
import com.inteliwallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        List<Achievement> allAchievements = achievementRepository.findAllByOrderByCreatedAtAsc();

        return allAchievements.stream()
            .map(achievement -> {
                UserAchievement userAchievement = userAchievementRepository
                    .findByUserIdAndAchievementId(userId, achievement.getId())
                    .orElse(null);

                return mapToResponse(achievement, userAchievement);
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> getUserUnlockedAchievements(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        List<UserAchievement> unlockedAchievements = userAchievementRepository
            .findByUserIdAndUnlockedTrue(userId);

        return unlockedAchievements.stream()
            .map(ua -> mapToResponse(ua.getAchievement(), ua))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAchievementsByCategory(String userId, String categoryStr) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Achievement.AchievementCategory category;
        try {
            category = Achievement.AchievementCategory.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Categoria de conquista inválida: " + categoryStr);
        }

        List<Achievement> achievements = achievementRepository.findByCategory(category);

        return achievements.stream()
            .map(achievement -> {
                UserAchievement userAchievement = userAchievementRepository
                    .findByUserIdAndAchievementId(userId, achievement.getId())
                    .orElse(null);

                return mapToResponse(achievement, userAchievement);
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void initializeUserAchievements(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        List<Achievement> allAchievements = achievementRepository.findAll();

        for (Achievement achievement : allAchievements) {
            if (!userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement.getId())) {
                UserAchievement userAchievement = new UserAchievement();
                userAchievement.setUser(user);
                userAchievement.setAchievement(achievement);
                userAchievement.setCurrentProgress(0);
                userAchievement.setUnlocked(false);
                userAchievementRepository.save(userAchievement);
            }
        }
    }

    @Transactional
    public void updateProgress(String userId, String achievementCode, int progressValue) {
        Achievement achievement = achievementRepository.findByCode(achievementCode)
            .orElse(null);

        if (achievement == null) {
            return;
        }

        UserAchievement userAchievement = userAchievementRepository
            .findByUserIdAndAchievementId(userId, achievement.getId())
            .orElse(null);

        if (userAchievement == null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return;

            userAchievement = new UserAchievement();
            userAchievement.setUser(user);
            userAchievement.setAchievement(achievement);
            userAchievement.setCurrentProgress(0);
            userAchievement.setUnlocked(false);
        }

        if (userAchievement.getUnlocked()) {
            return;
        }

        userAchievement.setCurrentProgress(progressValue);

        if (achievement.getTargetValue() != null &&
            userAchievement.getCurrentProgress() >= achievement.getTargetValue()) {
            userAchievement.unlock();

            User user = userAchievement.getUser();
            user.setTotalPoints(user.getTotalPoints() + achievement.getPoints());
            userRepository.save(user);
        }

        userAchievementRepository.save(userAchievement);
    }

    @Transactional
    public void incrementProgress(String userId, String achievementCode) {
        Achievement achievement = achievementRepository.findByCode(achievementCode)
            .orElse(null);

        if (achievement == null) {
            return;
        }

        UserAchievement userAchievement = userAchievementRepository
            .findByUserIdAndAchievementId(userId, achievement.getId())
            .orElse(null);

        if (userAchievement == null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return;

            userAchievement = new UserAchievement();
            userAchievement.setUser(user);
            userAchievement.setAchievement(achievement);
            userAchievement.setCurrentProgress(0);
            userAchievement.setUnlocked(false);
        }

        if (userAchievement.getUnlocked()) {
            return;
        }

        userAchievement.incrementProgress();

        if (achievement.getTargetValue() != null &&
            userAchievement.getCurrentProgress() >= achievement.getTargetValue()) {
            userAchievement.unlock();

            User user = userAchievement.getUser();
            user.setTotalPoints(user.getTotalPoints() + achievement.getPoints());
            userRepository.save(user);
        }

        userAchievementRepository.save(userAchievement);
    }

    private AchievementResponse mapToResponse(Achievement achievement, UserAchievement userAchievement) {
        AchievementResponse response = new AchievementResponse();
        response.setId(achievement.getId());
        response.setTitle(achievement.getTitle());
        response.setDescription(achievement.getDescription());
        response.setIcon(achievement.getIcon());
        response.setPoints(achievement.getPoints());
        response.setCode(achievement.getCode());
        response.setCategory(achievement.getCategory().name().toLowerCase());
        response.setTargetValue(achievement.getTargetValue());
        response.setCreatedAt(achievement.getCreatedAt());

        if (userAchievement != null) {
            response.setCurrentProgress(userAchievement.getCurrentProgress());
            response.setProgressPercentage(userAchievement.getProgressPercentage());
            response.setUnlocked(userAchievement.getUnlocked());
            response.setUnlockedAt(userAchievement.getUnlockedAt());
        } else {
            response.setCurrentProgress(0);
            response.setProgressPercentage(0);
            response.setUnlocked(false);
            response.setUnlockedAt(null);
        }

        return response;
    }
}
