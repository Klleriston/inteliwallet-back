package com.inteliwallet.repository;

import com.inteliwallet.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, String> {

    List<Achievement> findAllByOrderByCreatedAtAsc();

    Optional<Achievement> findByCode(String code);

    List<Achievement> findByCategory(Achievement.AchievementCategory category);
}
