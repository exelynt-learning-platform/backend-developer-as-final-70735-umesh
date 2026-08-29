package org.techhub.serviceimpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.techhub.dto.ReservationRequest;
import org.techhub.dto.ReservationResponse;
import org.techhub.entity.Reservation;
import org.techhub.entity.ReservationStatus;
import org.techhub.entity.Resource;
import org.techhub.entity.User;
import org.techhub.exception.ReservationNotFoundException;
import org.techhub.exception.ResourceNotFoundException;
import org.techhub.exception.UserNotFoundException;
import org.techhub.repository.ReservationRepository;
import org.techhub.repository.ResourceRepository;
import org.techhub.repository.UserRepository;
import org.techhub.service.ReservationService;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            ResourceRepository resourceRepository,
            UserRepository userRepository) {

        this.reservationRepository = reservationRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // CREATE RESERVATION
    // =====================================================

    @Override
    @Transactional
    public ReservationResponse createReservation(
            ReservationRequest request) {

        // Validate start and end time
        if (request.getStartTime() == null ||
                request.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Start time and end time are required");
        }

        // Start time must be before end time
        if (!request.getStartTime()
                .isBefore(request.getEndTime())) {

            throw new IllegalArgumentException(
                    "Start time must be before end time");
        }

        // Find resource
        Resource resource = resourceRepository
                .findById(request.getResourceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: "
                                        + request.getResourceId()));

        // Check resource availability
        if (!Boolean.TRUE.equals(resource.getAvailable())) {

            throw new IllegalStateException(
                    "Resource is not available");
        }

        // Check overlapping reservation
        boolean alreadyBooked =
                reservationRepository
                        .existsOverlappingReservation(
                                resource.getId(),
                                request.getStartTime(),
                                request.getEndTime(),
                                ReservationStatus.CANCELLED);

        if (alreadyBooked) {

            throw new IllegalStateException(
                    "Resource is already reserved for this time");
        }

        // =================================================
        // GET LOGGED-IN USER FROM JWT
        // =================================================

        String email = getLoggedInUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: " + email));

        // =================================================
        // CREATE RESERVATION
        // =================================================

        Reservation reservation = new Reservation();

        // User comes from JWT
        reservation.setUser(user);

        // Resource
        reservation.setResource(resource);

        // Start time
        reservation.setStartTime(
                request.getStartTime());

        // End time
        reservation.setEndTime(
                request.getEndTime());

        // Price
        reservation.setPrice(
                request.getPrice());

        // Purpose
        reservation.setPurpose(
                request.getPurpose());

        // Default status
        reservation.setStatus(
                ReservationStatus.PENDING);

        // Save reservation
        Reservation savedReservation =
                reservationRepository.save(reservation);

        return convertToResponse(savedReservation);
    }

    // =====================================================
    // USER - MY RESERVATIONS
    // =====================================================

    @Override
    public List<ReservationResponse> getMyReservations() {

        String email = getLoggedInUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with email: " + email));

        return reservationRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =====================================================
    // GET RESERVATION BY ID
    // USER -> OWN RESERVATION
    // ADMIN -> ANY RESERVATION
    // =====================================================

    @Override
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation =
                reservationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id));

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        // ADMIN can access any reservation
        if (authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ADMIN"))) {

            return convertToResponse(reservation);
        }

        // USER can access only own reservation
        String email = authentication.getName();

        if (!reservation.getUser()
                .getEmail()
                .equals(email)) {

            throw new AccessDeniedException(
                    "You are not authorized to access this reservation");
        }

        return convertToResponse(reservation);
    }

    // =====================================================
    // USER - CANCEL OWN RESERVATION
    // ADMIN - CANCEL ANY RESERVATION
    // =====================================================

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long id) {

        Reservation reservation =
                reservationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id));

        // Check if already cancelled
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Reservation is already cancelled");
        }

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        // Check if user is ADMIN
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        // If not ADMIN, check if user owns the reservation
        if (!isAdmin) {

            String email = getLoggedInUserEmail();

            if (!reservation.getUser()
                    .getEmail()
                    .equals(email)) {

                throw new AccessDeniedException(
                        "You can cancel only your own reservation");
            }
        }

        // Change status
        reservation.setStatus(
                ReservationStatus.CANCELLED);

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return convertToResponse(updatedReservation);
    }

    // =====================================================
    // ADMIN - GET ALL RESERVATIONS
    // =====================================================

    @Override
    public List<ReservationResponse> getAllReservations() {

        return reservationRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =====================================================
    // ADMIN - CONFIRM RESERVATION
    // =====================================================

    @Override
    @Transactional
    public ReservationResponse confirmReservation(Long id) {

        Reservation reservation =
                reservationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id));

        // Status checks
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot confirm a cancelled reservation");
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Reservation is already confirmed");
        }

        // Confirm reservation
        reservation.setStatus(
                ReservationStatus.CONFIRMED);

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return convertToResponse(updatedReservation);
    }

    // =====================================================
    // FILTER + PAGINATION + SORTING
    // =====================================================

    @Override
    public Page<ReservationResponse> filterReservations(
            ReservationStatus status,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        // Check if user is ADMIN
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        boolean isAdmin = authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        Page<Reservation> reservationPage;

        if (isAdmin) {
            // ADMIN can see all reservations
            reservationPage =
                    reservationRepository.filterReservations(
                            status,
                            minPrice,
                            maxPrice,
                            pageable);
        } else {
            // Regular USER can only see their own reservations
            String email = getLoggedInUserEmail();
            reservationPage =
                    reservationRepository.filterUserReservations(
                            email,
                            status,
                            minPrice,
                            maxPrice,
                            pageable);
        }

        // Convert entity page to response page
        return reservationPage
                .map(this::convertToResponse);
    }

    // =====================================================
    // GET LOGGED-IN USER EMAIL FROM JWT
    // =====================================================

    private String getLoggedInUserEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated");
        }

        return authentication.getName();
    }

    // =====================================================
    // ENTITY -> RESPONSE DTO
    // =====================================================

    private ReservationResponse convertToResponse(
            Reservation reservation) {

        return new ReservationResponse(

                reservation.getId(),

                reservation.getUser().getId(),

                reservation.getResource().getId(),

                reservation.getStartTime(),

                reservation.getEndTime(),

                reservation.getPrice(),

                reservation.getStatus(),

                reservation.getPurpose()
        );
    }
}