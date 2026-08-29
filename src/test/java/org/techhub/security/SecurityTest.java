package org.techhub.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import org.techhub.controller.ReservationController;
import org.techhub.service.ReservationService;

@WebMvcTest(ReservationController.class)
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Test
    void unauthenticatedUser_shouldBeUnauthorized()
            throws Exception {

        mockMvc.perform(
                get("/reservations/my")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUser_shouldAccessEndpoint()
            throws Exception {

        mockMvc.perform(
                get("/reservations/my")
                        .with(
                                user("user@gmail.com")
                                        .roles("USER")
                        )
        )
        .andExpect(status().isOk());
    }
}