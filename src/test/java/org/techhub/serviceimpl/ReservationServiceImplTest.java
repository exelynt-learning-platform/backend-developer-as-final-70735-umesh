package org.techhub.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.techhub.dto.ReservationRequest;
import org.techhub.dto.ReservationResponse;
import org.techhub.entity.Reservation;
import org.techhub.entity.ReservationStatus;
import org.techhub.entity.Resource;
import org.techhub.entity.User;
import org.techhub.repository.ReservationRepository;
import org.techhub.repository.ResourceRepository;
import org.techhub.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User user;
    private Resource resource;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("user@gmail.com");

        resource = new Resource();
        resource.setId(10L);
        resource.setAvailable(true);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@gmail.com",
                        null,
                        List.of(new SimpleGrantedAuthority("USER"))
                )
        );
    }

    @Test
    void createReservation_shouldCreateSuccessfully() {

        ReservationRequest request = new ReservationRequest();

        request.setResourceId(10L);
        request.setStartTime(
                LocalDateTime.now().plusDays(1)
        );
        request.setEndTime(
                LocalDateTime.now().plusDays(1).plusHours(2)
        );
        request.setPrice(500.0);
        request.setPurpose("Meeting");

        when(resourceRepository.findById(10L))
                .thenReturn(Optional.of(resource));

        when(reservationRepository.existsOverlappingReservation(
                eq(10L),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(ReservationStatus.CANCELLED)
        )).thenReturn(false);

        when(userRepository.findByEmail("user@gmail.com"))
                .thenReturn(Optional.of(user));

        Reservation savedReservation = new Reservation();

        savedReservation.setId(100L);
        savedReservation.setUser(user);
        savedReservation.setResource(resource);
        savedReservation.setStartTime(request.getStartTime());
        savedReservation.setEndTime(request.getEndTime());
        savedReservation.setPrice(500.0);
        savedReservation.setPurpose("Meeting");
        savedReservation.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(savedReservation);

        ReservationResponse response =
                reservationService.createReservation(request);

        assertEquals(100L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(10L, response.getResourceId());
        assertEquals(500.0, response.getPrice());
        assertEquals(
                ReservationStatus.PENDING,
                response.getStatus()
        );
    }

    @Test
    void createReservation_shouldFailWhenResourceNotFound() {

        ReservationRequest request = new ReservationRequest();

        request.setResourceId(999L);
        request.setStartTime(
                LocalDateTime.now().plusDays(1)
        );
        request.setEndTime(
                LocalDateTime.now().plusDays(1).plusHours(1)
        );
        request.setPrice(500.0);

        when(resourceRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .createReservation(request)
                );

        assertEquals(
                "Resource not found with id: 999",
                exception.getMessage()
        );
    }

    @Test
    void createReservation_shouldFailWhenResourceUnavailable() {

        ReservationRequest request = new ReservationRequest();

        request.setResourceId(10L);
        request.setStartTime(
                LocalDateTime.now().plusDays(1)
        );
        request.setEndTime(
                LocalDateTime.now().plusDays(1).plusHours(1)
        );
        request.setPrice(500.0);

        resource.setAvailable(false);

        when(resourceRepository.findById(10L))
                .thenReturn(Optional.of(resource));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .createReservation(request)
                );

        assertEquals(
                "Resource is not available",
                exception.getMessage()
        );
    }

    @Test
    void createReservation_shouldFailWhenTimeIsInvalid() {

        ReservationRequest request = new ReservationRequest();

        request.setResourceId(10L);

        request.setStartTime(
                LocalDateTime.now().plusDays(2)
        );

        request.setEndTime(
                LocalDateTime.now().plusDays(1)
        );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .createReservation(request)
                );

        assertEquals(
                "Start time must be before end time",
                exception.getMessage()
        );
    }

    @Test
    void createReservation_shouldFailWhenAlreadyBooked() {

        ReservationRequest request = new ReservationRequest();

        request.setResourceId(10L);

        request.setStartTime(
                LocalDateTime.now().plusDays(1)
        );

        request.setEndTime(
                LocalDateTime.now().plusDays(1).plusHours(2)
        );

        request.setPrice(500.0);

        when(resourceRepository.findById(10L))
                .thenReturn(Optional.of(resource));

        when(reservationRepository.existsOverlappingReservation(
                eq(10L),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(ReservationStatus.CANCELLED)
        )).thenReturn(true);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> reservationService
                                .createReservation(request)
                );

        assertEquals(
                "Resource is already reserved for this time",
                exception.getMessage()
        );
    }
}