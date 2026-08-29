package org.techhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import org.techhub.config.SecurityConfig;
import org.techhub.dto.ReservationResponse;
import org.techhub.entity.ReservationStatus;
import org.techhub.repository.UserRepository;
import org.techhub.security.JwtAuthenticationFilter;
import org.techhub.security.JwtService;
import org.techhub.service.ReservationService;
import org.techhub.service.UserSessionService;

@WebMvcTest(ReservationController.class)
@Import(SecurityConfig.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserSessionService userSessionService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void createReservation_shouldReturnSuccess()
            throws Exception {

        ReservationResponse response =
                new ReservationResponse(
                        1L,
                        1L,
                        10L,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(1).plusHours(2),
                        500.0,
                        ReservationStatus.PENDING,
                        "Meeting"
                );

        when(reservationService.createReservation(any()))
                .thenReturn(response);

        String json = """
                {
                    "resourceId": 10,
                    "startTime": "2026-09-01T10:00:00",
                    "endTime": "2026-09-01T12:00:00",
                    "price": 500.0,
                    "purpose": "Meeting"
                }
                """;

        mockMvc.perform(
                post("/reservations")
                        .with(csrf())
                        .with(user("user@gmail.com").authorities(new SimpleGrantedAuthority("USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isCreated());
    }

    @Test
    void createReservation_shouldFailValidation()
            throws Exception {

        String json = """
                {
                    "resourceId": null,
                    "startTime": null,
                    "endTime": null,
                    "price": 0,
                    "purpose": "Meeting"
                }
                """;

        mockMvc.perform(
                post("/reservations")
                        .with(csrf())
                        .with(user("user@gmail.com").authorities(new SimpleGrantedAuthority("USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void filterReservations_invalidPriceRange_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                get("/reservations/filter")
                        .with(user("user@gmail.com").authorities(new SimpleGrantedAuthority("USER")))
                        .param("minPrice", "500")
                        .param("maxPrice", "100")
        )
        .andExpect(status().isBadRequest());
    }
}