package org.techhub.service;

import org.techhub.dto.LoginRequest;
import org.techhub.dto.LoginResponse;
import org.techhub.dto.RegisterRequest;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    String register(RegisterRequest registerRequest);
}