package com.inteliwallet.repository;

import com.inteliwallet.entity.WaitlistUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistUserRepository extends JpaRepository<WaitlistUser, String> {

    Optional<WaitlistUser> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT w FROM WaitlistUser w WHERE w.notified = false ORDER BY w.createdAt ASC")
    List<WaitlistUser> findAllNotNotified();

    @Query("SELECT COUNT(w) FROM WaitlistUser w")
    long countTotal();
}
