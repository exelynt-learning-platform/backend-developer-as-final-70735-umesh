package org.techhub.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import org.techhub.entity.User;
import org.techhub.entity.UserSession;
import org.techhub.repository.UserSessionRepository;
import org.techhub.service.UserSessionService;

@Service
public class UserSessionServiceImpl
        implements UserSessionService {

    private final UserSessionRepository userSessionRepository;

    public UserSessionServiceImpl(
            UserSessionRepository userSessionRepository) {

        this.userSessionRepository =
                userSessionRepository;
    }

    // =====================================================
    // SAVE JWT SESSION
    // =====================================================

    @Override
    public void saveSession(
            User user,
            String token) {

        LocalDateTime createdAt =
                LocalDateTime.now();

        // JWT validity = 1 hour
        LocalDateTime expiresAt =
                createdAt.plusHours(1);

        UserSession session =
                new UserSession(
                        user,
                        token,
                        createdAt,
                        expiresAt);

        userSessionRepository.save(session);
    }

    // =====================================================
    // CHECK TOKEN ACTIVE
    // =====================================================

    @Override
    public boolean isTokenActive(
            String token) {

        UserSession session =
                userSessionRepository
                        .findByToken(token)
                        .orElse(null);

        if (session == null) {
            return false;
        }

        // Already logged out
        if (!Boolean.TRUE.equals(session.getActive())) {
            return false;
        }

        // Token expired
        if (LocalDateTime.now()
                .isAfter(session.getExpiresAt())) {

            session.setActive(false);

            userSessionRepository.save(session);

            return false;
        }

        return true;
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    @Override
    public void logout(
            String token) {

        UserSession session =
                userSessionRepository
                        .findByToken(token)
                        .orElse(null);

        if (session != null) {

            session.setActive(false);

            userSessionRepository.save(session);
        }
    }
}