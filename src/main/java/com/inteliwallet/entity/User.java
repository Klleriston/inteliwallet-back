package com.inteliwallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 10)
    private String avatar = "👤";

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(name = "has_completed_onboarding", nullable = false)
    private Boolean hasCompletedOnboarding = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserPlan plan = UserPlan.FREE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendInvite> sentInvites = new ArrayList<>();

    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendInvite> receivedInvites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeParticipant> challengeParticipations = new ArrayList<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengeGoal> createdChallenges = new ArrayList<>();
}
