package com.inteliwallet.repository;

import com.inteliwallet.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, String> {

    List<UserAchievement> findByUserIdOrderByUnlockedDesc(String userId);

    Optional<UserAchievement> findByUserIdAndAchievementId(String userId, String achievementId);

    Optional<UserAchievement> findByUserIdAndAchievement_Code(String userId, String achievementCode);

    List<UserAchievement> findByUserIdAndUnlockedTrue(String userId);

    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.unlocked = true")
    Long countUnlockedByUserId(@Param("userId") String userId);

    boolean existsByUserIdAndAchievementId(String userId, String achievementId);
}