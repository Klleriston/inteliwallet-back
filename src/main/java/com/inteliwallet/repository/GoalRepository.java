package com.inteliwallet.repository;

import com.inteliwallet.entity.Goal;
import com.inteliwallet.entity.Goal.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, String> {

    List<Goal> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Goal> findByUserIdAndStatus(String userId, GoalStatus status);

    Long countByUserIdAndStatus(String userId, GoalStatus status);

    @Query("SELECT COALESCE(SUM(g.targetAmount), 0) FROM Goal g " +
           "WHERE g.user.id = :userId AND g.status = 'ACTIVE'")
    BigDecimal sumTargetAmountByUserId(@Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(g.currentAmount), 0) FROM Goal g " +
           "WHERE g.user.id = :userId AND g.status = 'ACTIVE'")
    BigDecimal sumCurrentAmountByUserId(@Param("userId") String userId);
}