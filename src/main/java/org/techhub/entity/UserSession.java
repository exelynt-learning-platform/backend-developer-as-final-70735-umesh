package org.techhub.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who owns this session
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // JWT token
    @Column(name = "token", nullable = false, unique = true, length = 1000)
    private String token;

    // Session creation time
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Token/session expiration time
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // Logout / revoke status
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public UserSession(
            User user,
            String token,
            LocalDateTime createdAt,
            LocalDateTime expiresAt) {

        this.user = user;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = true;
    }
}