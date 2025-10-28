package com.inteliwallet.repository;

import com.inteliwallet.entity.ChallengeStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeStreakRepository extends JpaRepository<ChallengeStreak, String> {

    Optional<ChallengeStreak> findByParticipantId(String participantId);

    List<ChallengeStreak> findByChallengeGoalId(String challengeGoalId);

    @Query("SELECT cs FROM ChallengeStreak cs " +
           "WHERE cs.participant.user.id = :userId " +
           "ORDER BY cs.currentStreak DESC")
    List<ChallengeStreak> findByUserId(@Param("userId") String userId);

    @Query("SELECT cs FROM ChallengeStreak cs " +
           "WHERE cs.challengeGoal.id = :challengeGoalId " +
           "ORDER BY cs.currentStreak DESC")
    List<ChallengeStreak> findTopStreaksByChallengeId(@Param("challengeGoalId") String challengeGoalId);
}