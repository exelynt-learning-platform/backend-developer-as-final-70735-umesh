package org.techhub.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.techhub.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    private final SecretKey secretKey;

    // Constructor
    public JwtService(JwtConfig jwtConfig) {

        this.jwtConfig = jwtConfig;

        this.secretKey = Keys.hmacShaKeyFor(
                jwtConfig.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    // =====================================================
    // GENERATE JWT TOKEN
    // =====================================================

    public String generateToken(String email) {

        Date currentDate = new Date();

        Date expiryDate = new Date(
                currentDate.getTime()
                        + jwtConfig.getExpiration()
        );

        return Jwts.builder()
                .subject(email)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // =====================================================
    // GET EMAIL FROM TOKEN
    // =====================================================

    public String getEmailFromToken(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // =====================================================
    // CHECK TOKEN VALID
    // =====================================================

    public boolean isTokenValid(String token) {

        try {

            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    // =====================================================
    // CHECK TOKEN EXPIRATION
    // =====================================================

    public boolean isTokenExpired(String token) {

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration()
                    .before(new Date());

        } catch (Exception e) {

            return true;
        }
    }
}