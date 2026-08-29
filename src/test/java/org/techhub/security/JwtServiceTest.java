package org.techhub.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateToken_shouldGenerateToken() {

        String token =
                jwtService.generateToken("user@gmail.com");

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void token_shouldContainCorrectUsername() {

        String email = "user@gmail.com";

        String token =
                jwtService.generateToken(email);

        String extractedEmail =
                jwtService.getEmailFromToken(token);

        assertEquals(email, extractedEmail);
        assertTrue(jwtService.isTokenValid(token));
    }
}