package com.inteliwallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist_users", indexes = {
        @Index(name = "idx_waitlist_email", columnList = "email", unique = true),
        @Index(name = "idx_waitlist_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitlistUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "temporary_password")
    private String temporaryPassword;

    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent = false;

    @Column(name = "notified", nullable = false)
    private Boolean notified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;
}
