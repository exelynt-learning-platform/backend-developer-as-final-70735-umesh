package org.techhub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.techhub.entity.UserSession;

public interface UserSessionRepository
        extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByToken(String token);

    boolean existsByTokenAndActiveTrue(String token);
}