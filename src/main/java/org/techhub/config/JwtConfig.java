package org.techhub.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration:86400000}")
	private long expiration;

	@PostConstruct
	public void validateConfiguration() {
		if (secret == null || secret.trim().isEmpty()) {
			throw new IllegalStateException("JWT secret must be configured via jwt.secret / JWT_SECRET environment variable.");
		}
		if (secret.trim().length() < 32) {
			throw new IllegalStateException("JWT secret is too short for secure HMAC-SHA256 signing (must be at least 32 characters).");
		}
	}

	public String getSecret() {
		return secret;
	}

	public long getExpiration() {
		return expiration;
	}
}