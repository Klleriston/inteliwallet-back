package com.inteliwallet.service;

import com.inteliwallet.dto.response.ChallengeStreakResponse;
import com.inteliwallet.dto.response.UserStreakResponse;
import com.inteliwallet.entity.*;
import com.inteliwallet.entity.UserStreak.StreakType;
import com.inteliwallet.exception.ResourceNotFoundException;
import com.inteliwallet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreakService {

    private final UserStreakRepository userStreakRepository;
    private final ChallengeStreakRepository challengeStreakRepository;
    private final UserRepository userRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final AchievementService achievementService;


    @Transactional
    public UserStreakResponse recordActivity(String userId, StreakType streakType) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        UserStreak streak = userStreakRepository
            .findByUserIdAndStreakType(userId, streakType)
            .orElseGet(() -> {
                UserStreak newStreak = new UserStreak();
                newStreak.setUser(user);
                newStreak.setStreakType(streakType);
                return newStreak;
            });

        LocalDate today = LocalDate.now();
        streak.checkAndUpdateStreak(today);
        streak.incrementStreak();

        streak = userStreakRepository.save(streak);
        return mapToUserStreakResponse(streak);
    }

    @Transactional
    public UserStreakResponse recordDailyLogin(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        UserStreak streak = userStreakRepository
            .findByUserIdAndStreakType(userId, StreakType.DAILY_LOGIN)
            .orElseGet(() -> {
                UserStreak newStreak = new UserStreak();
                newStreak.setUser(user);
                newStreak.setStreakType(StreakType.DAILY_LOGIN);
                return newStreak;
            });

        LocalDate today = LocalDate.now();

        if (streak.getLastActivityDate() != null && streak.getLastActivityDate().equals(today)) {
            log.info("Login já registrado hoje para usuário {}", userId);
            return mapToUserStreakResponse(streak);
        }

        streak.checkAndUpdateStreak(today);
        streak.incrementStreak();
        streak = userStreakRepository.save(streak);

        try {
            updateStreakAchievements(userId, streak.getCurrentStreak());
        } catch (Exception e) {
            log.error("Erro ao atualizar conquistas de streak para usuário {}: {}", userId, e.getMessage());
        }

        log.info("Streak de login registrada para usuário {}: {} dias", userId, streak.getCurrentStreak());
        return mapToUserStreakResponse(streak);
    }

    private void updateStreakAchievements(String userId, Integer currentStreak) {
        if (currentStreak >= 7) {
            achievementService.updateProgress(userId, "STREAK_7_DAYS", currentStreak);
        }
        if (currentStreak >= 30) {
            achievementService.updateProgress(userId, "STREAK_30_DAYS", currentStreak);
        }
        if (currentStreak >= 90) {
            achievementService.updateProgress(userId, "STREAK_90_DAYS", currentStreak);
        }
        if (currentStreak >= 180) {
            achievementService.updateProgress(userId, "STREAK_180_DAYS", currentStreak);
        }
        if (currentStreak >= 365) {
            achievementService.updateProgress(userId, "STREAK_365_DAYS", currentStreak);
        }
    }

    @Transactional(readOnly = true)
    public List<UserStreakResponse> getUserStreaks(String userId) {
        List<UserStreak> streaks = userStreakRepository.findByUserId(userId);
        return streaks.stream()
            .map(this::mapToUserStreakResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserStreakResponse> getActiveUserStreaks(String userId) {
        List<UserStreak> streaks = userStreakRepository.findActiveStreaksByUserId(userId);
        return streaks.stream()
            .map(this::mapToUserStreakResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserStreakResponse getUserStreakByType(String userId, StreakType streakType) {
        UserStreak streak = userStreakRepository
            .findByUserIdAndStreakType(userId, streakType)
            .orElseThrow(() -> new ResourceNotFoundException("Streak não encontrado"));
        return mapToUserStreakResponse(streak);
    }


    @Transactional
    public ChallengeStreakResponse recordChallengeContribution(String participantId) {
        ChallengeParticipant participant = participantRepository.findById(participantId)
            .orElseThrow(() -> new ResourceNotFoundException("Participante não encontrado"));

        ChallengeStreak streak = challengeStreakRepository
            .findByParticipantId(participantId)
            .orElseGet(() -> {
                ChallengeStreak newStreak = new ChallengeStreak();
                newStreak.setParticipant(participant);
                newStreak.setChallengeGoal(participant.getChallengeGoal());
                return newStreak;
            });

        LocalDate today = LocalDate.now();
        streak.checkAndUpdateStreak(today);
        streak.recordContribution();

        if (streak.getBonusPointsEarned() > 0 && !participant.getRewardClaimed()) {
            User user = participant.getUser();
            user.setTotalPoints(user.getTotalPoints() + streak.getBonusPointsEarned());
            userRepository.save(user);
        }

        streak = challengeStreakRepository.save(streak);
        return mapToChallengeStreakResponse(streak);
    }


    @Transactional(readOnly = true)
    public List<ChallengeStreakResponse> getUserChallengeStreaks(String userId) {
        List<ChallengeStreak> streaks = challengeStreakRepository.findByUserId(userId);
        return streaks.stream()
            .map(this::mapToChallengeStreakResponse)
            .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ChallengeStreakResponse> getChallengeStreaks(String challengeGoalId) {
        List<ChallengeStreak> streaks = challengeStreakRepository
            .findTopStreaksByChallengeId(challengeGoalId);
        return streaks.stream()
            .map(this::mapToChallengeStreakResponse)
            .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ChallengeStreakResponse getChallengeStreakByParticipant(String participantId) {
        ChallengeStreak streak = challengeStreakRepository
            .findByParticipantId(participantId)
            .orElseThrow(() -> new ResourceNotFoundException("Streak de desafio não encontrado"));
        return mapToChallengeStreakResponse(streak);
    }


    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void checkAndUpdateAllStreaks() {
        log.info("Iniciando verificação diária de streaks...");
        LocalDate today = LocalDate.now();

        List<UserStreak> allUserStreaks = userStreakRepository.findAll();
        for (UserStreak streak : allUserStreaks) {
            streak.checkAndUpdateStreak(today);
            userStreakRepository.save(streak);
        }

        List<ChallengeStreak> allChallengeStreaks = challengeStreakRepository.findAll();
        for (ChallengeStreak streak : allChallengeStreaks) {
            streak.checkAndUpdateStreak(today);
            challengeStreakRepository.save(streak);
        }

        log.info("Verificação de streaks concluída.");
    }

    private UserStreakResponse mapToUserStreakResponse(UserStreak streak) {
        UserStreakResponse response = new UserStreakResponse();
        response.setId(streak.getId());
        response.setStreakType(streak.getStreakType().getValue());
        response.setStreakTypeName(streak.getStreakType().getDisplayName());
        response.setCurrentStreak(streak.getCurrentStreak());
        response.setLongestStreak(streak.getLongestStreak());
        response.setLastActivityDate(streak.getLastActivityDate());
        response.setTotalDaysActive(streak.getTotalDaysActive());
        response.setIsActive(streak.getIsActive());
        response.setCreatedAt(streak.getCreatedAt());
        response.setUpdatedAt(streak.getUpdatedAt());
        return response;
    }

    private ChallengeStreakResponse mapToChallengeStreakResponse(ChallengeStreak streak) {
        ChallengeStreakResponse response = new ChallengeStreakResponse();
        response.setId(streak.getId());
        response.setParticipantId(streak.getParticipant().getId());
        response.setChallengeGoalId(streak.getChallengeGoal().getId());
        response.setChallengeTitle(streak.getChallengeGoal().getTitle());
        response.setCurrentStreak(streak.getCurrentStreak());
        response.setLongestStreak(streak.getLongestStreak());
        response.setLastContributionDate(streak.getLastContributionDate());
        response.setTotalContributions(streak.getTotalContributions());
        response.setStreakActive(streak.getStreakActive());
        response.setBonusPointsEarned(streak.getBonusPointsEarned());
        response.setCurrentStreakBonus(streak.calculateStreakBonus());
        response.setCreatedAt(streak.getCreatedAt());
        response.setUpdatedAt(streak.getUpdatedAt());
        return response;
    }
}
