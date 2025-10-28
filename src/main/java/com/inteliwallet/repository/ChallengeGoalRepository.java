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
           "JOIN c.participants p " +
           "WHERE p.user.id = :userId " +
           "ORDER BY c.createdAt DESC")
    List<ChallengeGoal> findByParticipantUserId(@Param("userId") String userId);

    @Query("SELECT c FROM ChallengeGoal c " +
           "JOIN c.participants p " +
           "WHERE p.user.id = :userId AND c.status = :status " +
           "ORDER BY c.createdAt DESC")
    List<ChallengeGoal> findByParticipantUserIdAndStatus(
        @Param("userId") String userId,
        @Param("status") ChallengeStatus status
    );

    @Query("SELECT COUNT(c) FROM ChallengeGoal c " +
           "WHERE c.creator.id = :userId AND c.status = com.inteliwallet.entity.ChallengeGoal$ChallengeStatus.ACTIVE")
    Long countActiveCreatedChallengesByUserId(@Param("userId") String userId);

    @Query("SELECT c FROM ChallengeGoal c " +
           "WHERE c.status = com.inteliwallet.entity.ChallengeGoal$ChallengeStatus.ACTIVE " +
           "AND SIZE(c.participants) < c.maxParticipants " +
           "ORDER BY c.createdAt DESC")
    List<ChallengeGoal> findAvailableChallenges();
}
