package org.techhub.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.techhub.entity.Reservation;
import org.techhub.entity.Resource;
import org.techhub.entity.User;
import org.techhub.repository.ReservationRepository;
import org.techhub.repository.ResourceRepository;
import org.techhub.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReservationOwnershipTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation reservation;

    @BeforeEach
    void setUp() {

        User owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@gmail.com");

        Resource resource = new Resource();
        resource.setId(10L);

        reservation = new Reservation();

        reservation.setId(100L);
        reservation.setUser(owner);
        reservation.setResource(resource);
    }

    @Test
    void owner_shouldAccessOwnReservation() {

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "owner@gmail.com",
                                null
                        )
                );

        when(reservationRepository.findById(100L))
                .thenReturn(Optional.of(reservation));

        var response =
                reservationService
                        .getReservationById(100L);

        assertEquals(
                100L,
                response.getId()
        );
    }

    @Test
    void anotherUser_shouldNotAccessReservation() {

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "other@gmail.com",
                                null
                        )
                );

        when(reservationRepository.findById(100L))
                .thenReturn(Optional.of(reservation));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .getReservationById(100L)
                );

        assertEquals(
                "You are not authorized to access this reservation",
                exception.getMessage()
        );
    }
}