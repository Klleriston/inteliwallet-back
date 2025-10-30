package com.inteliwallet.service;

import com.inteliwallet.dto.response.LeaderboardResponse;
import com.inteliwallet.entity.User;
import com.inteliwallet.repository.UserAchievementRepository;
import com.inteliwallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;
    private final UserAchievementRepository userAchievementRepository;

    @Transactional(readOnly = true)
    public List<LeaderboardResponse> getGlobalLeaderboard(String currentUserId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 50;
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "totalPoints"));
        List<User> topUsers = userRepository.findAll(pageable).getContent();

        List<LeaderboardResponse> leaderboard = new ArrayList<>();
        int rank = 1;

        for (User user : topUsers) {
            LeaderboardResponse response = new LeaderboardResponse();
            response.setRank(rank++);
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setAvatar(user.getAvatar());
            response.setTotalPoints(user.getTotalPoints());
            response.setLevel(user.getLevel());

            Long achievementsCount = userAchievementRepository.countUnlockedByUserId(user.getId());
            response.setAchievementsUnlocked(achievementsCount.intValue());
            response.setIsCurrentUser(user.getId().equals(currentUserId));

            leaderboard.add(response);
        }

        boolean currentUserInTop = leaderboard.stream()
            .anyMatch(LeaderboardResponse::getIsCurrentUser);

        if (!currentUserInTop && currentUserId != null) {
            User currentUser = userRepository.findById(currentUserId).orElse(null);
            if (currentUser != null) {
                int userRank = calculateUserRank(currentUser);
                LeaderboardResponse currentUserResponse = new LeaderboardResponse();
                currentUserResponse.setRank(userRank);
                currentUserResponse.setUserId(currentUser.getId());
                currentUserResponse.setUsername(currentUser.getUsername());
                currentUserResponse.setAvatar(currentUser.getAvatar());
                currentUserResponse.setTotalPoints(currentUser.getTotalPoints());
                currentUserResponse.setLevel(currentUser.getLevel());

                Long achievementsCount = userAchievementRepository.countUnlockedByUserId(currentUser.getId());
                currentUserResponse.setAchievementsUnlocked(achievementsCount.intValue());
                currentUserResponse.setIsCurrentUser(true);

                leaderboard.add(currentUserResponse);
            }
        }

        return leaderboard;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardResponse> getFriendsLeaderboard(String currentUserId) {
        List<LeaderboardResponse> leaderboard = new ArrayList<>();

        User currentUser = userRepository.findById(currentUserId).orElse(null);
        if (currentUser != null) {
            LeaderboardResponse response = new LeaderboardResponse();
            response.setRank(1);
            response.setUserId(currentUser.getId());
            response.setUsername(currentUser.getUsername());
            response.setAvatar(currentUser.getAvatar());
            response.setTotalPoints(currentUser.getTotalPoints());
            response.setLevel(currentUser.getLevel());

            Long achievementsCount = userAchievementRepository.countUnlockedByUserId(currentUser.getId());
            response.setAchievementsUnlocked(achievementsCount.intValue());
            response.setIsCurrentUser(true);

            leaderboard.add(response);
        }

        return leaderboard;
    }

    private int calculateUserRank(User user) {
        Long countHigher = userRepository.countByTotalPointsGreaterThan(user.getTotalPoints());
        return countHigher.intValue() + 1;
    }
}
