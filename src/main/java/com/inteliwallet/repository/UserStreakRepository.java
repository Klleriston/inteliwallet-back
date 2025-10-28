package com.inteliwallet.repository;

import com.inteliwallet.entity.UserStreak;
import com.inteliwallet.entity.UserStreak.StreakType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, String> {

    List<UserStreak> findByUserId(String userId);

    Optional<UserStreak> findByUserIdAndStreakType(String userId, StreakType streakType);

    @Query("SELECT s FROM UserStreak s WHERE s.user.id = :userId AND s.isActive = true")
    List<UserStreak> findActiveStreaksByUserId(@Param("userId") String userId);

    @Query("SELECT s FROM UserStreak s WHERE s.user.id = :userId ORDER BY s.currentStreak DESC")
    List<UserStreak> findTopStreaksByUserId(@Param("userId") String userId);

    @Query("SELECT s FROM UserStreak s WHERE s.streakType = :streakType ORDER BY s.currentStreak DESC")
    List<UserStreak> findTopStreaksByType(@Param("streakType") StreakType streakType);
}
