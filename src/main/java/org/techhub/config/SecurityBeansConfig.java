package org.techhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.techhub.repository.UserRepository;

@Configuration
public class SecurityBeansConfig {

    private final UserRepository userRepository;

    public SecurityBeansConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =====================================================
    // AUTHENTICATION MANAGER
    // =====================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    

    // =====================================================
    // USER DETAILS SERVICE
    // =====================================================

    @Bean
    public UserDetailsService userDetailsService() {

        return username -> userRepository.findByEmail(username)

                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(
                                user.getUserRoles()
                                        .stream()
                                        .map(userRole ->
                                                new SimpleGrantedAuthority(
                                                        userRole.getRole().getName()
                                                )
                                        )
                                        .toList()
                        )
                        .disabled(
                                !Boolean.TRUE.equals(user.getEnabled())
                        )
                        .build()
                )

                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: " + username
                        )
                );
    }
}