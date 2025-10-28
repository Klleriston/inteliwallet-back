package com.inteliwallet.repository;

import com.inteliwallet.entity.ChallengeParticipant;
import com.inteliwallet.entity.ChallengeParticipant.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, String> {

    List<ChallengeParticipant> findByChallengeGoalId(String challengeGoalId);

    List<ChallengeParticipant> findByUserId(String userId);

    List<ChallengeParticipant> findByUserIdAndStatus(String userId, ParticipantStatus status);

    Optional<ChallengeParticipant> findByChallengeGoalIdAndUserId(String challengeGoalId, String userId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM ChallengeParticipant p " +
           "WHERE p.challengeGoal.id = :challengeGoalId AND p.user.id = :userId")
    Boolean existsByChallengeGoalIdAndUserId(
        @Param("challengeGoalId") String challengeGoalId,
        @Param("userId") String userId
    );

    @Query("SELECT COUNT(p) FROM ChallengeParticipant p " +
           "WHERE p.challengeGoal.id = :challengeGoalId AND p.status = com.inteliwallet.entity.ChallengeParticipant$ParticipantStatus.ACTIVE")
    Long countActiveByChallengeGoalId(@Param("challengeGoalId") String challengeGoalId);

    @Query("SELECT p FROM ChallengeParticipant p " +
           "WHERE p.challengeGoal.id = :challengeGoalId " +
           "ORDER BY p.contributedAmount DESC")
    List<ChallengeParticipant> findTopContributorsByChallengeGoalId(@Param("challengeGoalId") String challengeGoalId);
}