package org.techhub.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.techhub.dto.LoginRequest;
import org.techhub.dto.LoginResponse;
import org.techhub.dto.RegisterRequest;
import org.techhub.service.AuthService;
import org.techhub.service.UserSessionService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserSessionService userSessionService;

    public AuthController(
            AuthService authService,
            UserSessionService userSessionService) {

        this.authService = authService;
        this.userSessionService = userSessionService;
    }

    // =====================================================
    // REGISTER
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        String message =
                authService.register(registerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(message);
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse response =
                authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid Authorization header");
        }

        String token = authorization.substring(7);

        userSessionService.logout(token);

        return ResponseEntity.ok(
                "Logout successful");
    }
}