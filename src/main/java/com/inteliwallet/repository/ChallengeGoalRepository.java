package com.inteliwallet.repository;

import com.inteliwallet.entity.ChallengeGoal;
import com.inteliwallet.entity.ChallengeGoal.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeGoalRepository extends JpaRepository<ChallengeGoal, String> {

    List<ChallengeGoal> findByCreatorIdAndStatus(String creatorId, ChallengeStatus status);

    List<ChallengeGoal> findByCreatorId(String creatorId);

    List<ChallengeGoal> findByStatus(ChallengeStatus status);

    @Query("SELECT c FROM ChallengeGoal c WHERE c.status = :status ORDER BY c.createdAt DESC")
    List<ChallengeGoal> findActiveChallenge(@Param("status") ChallengeStatus status);

    @Query("SELECT c FROM ChallengeGoal c " +
           "JOIN FETCH c.creator " +
           "JOIN c.participants p " +
           "WHERE p.user.id = :userId " +
           "ORDER BY c.createdAt DESC")
    List<ChallengeGoal> findByParticipantUserId(@Param("userId") String userId);

    @Query("SELECT c FROM ChallengeGoal c " +
           "JOIN FETCH c.creator " +
           "JOIN c.participants p " +
           "WHERE p.user.id = :userId AND c.status = :status " +
           "ORDER BY c.createdAt DESC")
    List<ChallengeGoal> findByParticipantUserIdAndStatus(
        @Param("userId") String userId,
        @Param("status") ChallengeStatus status
    );

    @Query("SELECT COUNT(c) FROM ChallengeGoal c " +
           "WHERE c.creator.id = :userId AND c.status = 'ACTIVE'")
    Long countActiveCreatedChallengesByUserId(@Param("userId") String userId);

    @Query("SELECT DISTINCT c FROM ChallengeGoal c " +
           "JOIN FETCH c.creator " +
           "LEFT JOIN c.participants p " +
           "WHERE c.status = 'ACTIVE' " +
           "AND (c.maxParticipants IS NULL OR " +
           "(SELECT COUNT(p2) FROM ChallengeParticipant p2 WHERE p2.challengeGoal.id = c.id AND p2.status = 'ACTIVE') < c.maxParticipants) " +
           "ORDER BY c.createdAt DESC")
    List<ChallengeGoal> findAvailableChallenges();
}
