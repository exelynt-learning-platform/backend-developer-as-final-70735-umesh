package org.techhub.service;

import org.techhub.entity.User;

public interface UserSessionService {

    void saveSession(
            User user,
            String token);

    boolean isTokenActive(
            String token);

    void logout(
            String token);
}