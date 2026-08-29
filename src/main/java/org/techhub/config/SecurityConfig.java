package org.techhub.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.techhub.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
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
                // EXCEPTION HANDLING - 401 FOR UNAUTHENTICATED
                // =================================================

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized: " + authException.getMessage()
                                )
                        )
                )

                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // AUTH APIs
                        // =================================================

                        .requestMatchers(HttpMethod.POST, "/auth/logout")
                        .authenticated()

                        .requestMatchers("/auth/login", "/auth/register")
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

                        // USER + ADMIN -> FILTER RESERVATIONS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reservations/filter"
                        )
                        .hasAnyAuthority("USER", "ADMIN")

                        // ADMIN -> ALL ADMIN RESERVATION ROUTES
                        .requestMatchers(
                                "/reservations/admin/**"
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