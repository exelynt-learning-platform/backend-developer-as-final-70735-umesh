package org.techhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.techhub.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf -> csrf.disable())

                // =================================================
                // JWT - STATELESS
                // =================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // AUTH APIs
                        // =================================================

                        .requestMatchers("/auth/**")
                        .permitAll()

                        // =================================================
                        // SWAGGER APIs
                        // =================================================

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // =================================================
                        // RESOURCE MODULE
                        // =================================================

                        // USER + ADMIN -> GET
                        .requestMatchers(
                                HttpMethod.GET,
                                "/resources/**"
                        )
                        .hasAnyAuthority("USER", "ADMIN")

                        // ADMIN -> POST
                        .requestMatchers(
                                HttpMethod.POST,
                                "/resources/**"
                        )
                        .hasAuthority("ADMIN")

                        // ADMIN -> PUT
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/resources/**"
                        )
                        .hasAuthority("ADMIN")

                        // ADMIN -> DELETE
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/resources/**"
                        )
                        .hasAuthority("ADMIN")

                        // =================================================
                        // RESERVATION MODULE
                        // =================================================

                        // USER + ADMIN -> CREATE RESERVATION
                        .requestMatchers(
                                HttpMethod.POST,
                                "/reservations"
                        )
                        .hasAnyAuthority("USER", "ADMIN")

                        // USER + ADMIN -> MY RESERVATIONS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reservations/my"
                        )
                        .hasAnyAuthority("USER", "ADMIN")

                        // ADMIN -> ALL RESERVATIONS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reservations/admin/all"
                        )
                        .hasAuthority("ADMIN")

                        // ADMIN -> CONFIRM RESERVATION
                        .requestMatchers(
                                HttpMethod.POST,
                                "/reservations/admin/*/confirm"
                        )
                        .hasAuthority("ADMIN")

                        // ADMIN -> DELETE RESERVATION
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/reservations/admin/*"
                        )
                        .hasAuthority("ADMIN")

                        // USER + ADMIN -> GET RESERVATION BY ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reservations/*"
                        )
                        .hasAnyAuthority("USER", "ADMIN")

                        // USER + ADMIN -> CANCEL RESERVATION
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/reservations/*"
                        )
                        .hasAnyAuthority("USER", "ADMIN")

                        // =================================================
                        // OTHER APIs
                        // =================================================

                        .anyRequest()
                        .authenticated()
                )

                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}