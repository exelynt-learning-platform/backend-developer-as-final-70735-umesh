package org.techhub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import org.techhub.dto.ReservationResponse;
import org.techhub.entity.ReservationStatus;
import org.techhub.service.ReservationService;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isOk());
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
        .andExpect(status().isBadRequest());
    }
}