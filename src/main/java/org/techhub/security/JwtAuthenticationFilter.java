package org.techhub.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    // Constructor
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // =====================================================
        // GET AUTHORIZATION HEADER
        // =====================================================

        String authorizationHeader =
                request.getHeader("Authorization");

        // JWT token nahi hai
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // =====================================================
        // GET TOKEN
        // =====================================================

        String token =
                authorizationHeader.substring(7);

        try {

            // =================================================
            // CHECK TOKEN VALID
            // =================================================

            if (!jwtService.isTokenValid(token)) {

                filterChain.doFilter(request, response);
                return;
            }

            // =================================================
            // CHECK TOKEN EXPIRED
            // =================================================

            if (jwtService.isTokenExpired(token)) {

                filterChain.doFilter(request, response);
                return;
            }

            // =================================================
            // GET EMAIL FROM TOKEN
            // =================================================

            String email =
                    jwtService.getEmailFromToken(token);

            // =================================================
            // CHECK SECURITY CONTEXT
            // =================================================

            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                // =============================================
                // LOAD USER FROM DATABASE
                // =============================================

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(email);

                // =============================================
                // CREATE AUTHENTICATION
                // =============================================

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // =============================================
                // SET REQUEST DETAILS
                // =============================================

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // =============================================
                // SET SECURITY CONTEXT
                // =============================================

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }

        } catch (Exception e) {

            // Invalid / expired / malformed JWT
            SecurityContextHolder
                    .clearContext();
        }

        // =====================================================
        // CONTINUE REQUEST
        // =====================================================

        filterChain.doFilter(request, response);
    }
}