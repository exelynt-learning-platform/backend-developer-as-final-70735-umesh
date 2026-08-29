package org.techhub.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.techhub.dto.ReservationRequest;
import org.techhub.dto.ReservationResponse;
import org.techhub.entity.Reservation;
import org.techhub.entity.ReservationStatus;
import org.techhub.entity.Resource;
import org.techhub.entity.User;
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
    public ReservationResponse createReservation(
            ReservationRequest request) {

        // Validate start and end time
        if (request.getStartTime() == null ||
                request.getEndTime() == null) {

            throw new RuntimeException(
                    "Start time and end time are required");
        }

        // Start time must be before end time
        if (!request.getStartTime()
                .isBefore(request.getEndTime())) {

            throw new RuntimeException(
                    "Start time must be before end time");
        }

        // Find resource
        Resource resource = resourceRepository
                .findById(request.getResourceId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resource not found with id: "
                                        + request.getResourceId()));

        // Check resource availability
        if (!Boolean.TRUE.equals(resource.getAvailable())) {

            throw new RuntimeException(
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

            throw new RuntimeException(
                    "Resource is already reserved for this time");
        }

        // =================================================
        // GET LOGGED-IN USER FROM JWT
        // =================================================

        String email = getLoggedInUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

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
                        new RuntimeException(
                                "User not found"));

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
                                new RuntimeException(
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

            throw new RuntimeException(
                    "You are not authorized to access this reservation");
        }

        return convertToResponse(reservation);
    }

    // =====================================================
    // USER - CANCEL OWN RESERVATION
    // =====================================================

    @Override
    public ReservationResponse cancelReservation(Long id) {

        Reservation reservation =
                reservationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found with id: "
                                                + id));

        String email = getLoggedInUserEmail();

        // User can cancel only own reservation
        if (!reservation.getUser()
                .getEmail()
                .equals(email)) {

            throw new RuntimeException(
                    "You can cancel only your own reservation");
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
    public ReservationResponse confirmReservation(Long id) {

        Reservation reservation =
                reservationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found with id: "
                                                + id));

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

        // Create mutable list
        List<Reservation> reservations =
                new ArrayList<>(
                        reservationRepository.findAll());

        // =================================================
        // STATUS FILTER
        // =================================================

        if (status != null) {

            reservations.removeIf(r ->
                    r.getStatus() != status);
        }

        // =================================================
        // MIN PRICE FILTER
        // =================================================

        if (minPrice != null) {

            reservations.removeIf(r ->
                    r.getPrice() == null ||
                    r.getPrice() < minPrice);
        }

        // =================================================
        // MAX PRICE FILTER
        // =================================================

        if (maxPrice != null) {

            reservations.removeIf(r ->
                    r.getPrice() == null ||
                    r.getPrice() > maxPrice);
        }

        // =================================================
        // SORTING
        // =================================================

        if (pageable.getSort().isSorted()) {

            pageable.getSort()
                    .forEach(order -> {

                        String property =
                                order.getProperty();

                        boolean ascending =
                                order.isAscending();

                        // =================================================
                        // SORT BY PRICE
                        // =================================================

                        if ("price"
                                .equalsIgnoreCase(property)) {

                            reservations.sort((r1, r2) -> {

                                Double price1 =
                                        r1.getPrice();

                                Double price2 =
                                        r2.getPrice();

                                if (price1 == null) {
                                    price1 = 0.0;
                                }

                                if (price2 == null) {
                                    price2 = 0.0;
                                }

                                int result =
                                        Double.compare(
                                                price1,
                                                price2);

                                return ascending
                                        ? result
                                        : -result;
                            });
                        }

                        // =================================================
                        // SORT BY START TIME
                        // =================================================

                        if ("startTime"
                                .equalsIgnoreCase(property)) {

                            reservations.sort((r1, r2) -> {

                                int result =
                                        r1.getStartTime()
                                                .compareTo(
                                                        r2.getStartTime());

                                return ascending
                                        ? result
                                        : -result;
                            });
                        }
                    });
        }

        // =================================================
        // PAGINATION
        // =================================================

        int page =
                pageable.getPageNumber();

        int size =
                pageable.getPageSize();

        int start =
                page * size;

        int end =
                Math.min(
                        start + size,
                        reservations.size());

        List<Reservation> pageContent;

        if (start >= reservations.size()) {

            pageContent = List.of();

        } else {

            pageContent =
                    reservations.subList(
                            start,
                            end);
        }

        // =================================================
        // CONVERT ENTITY TO DTO
        // =================================================

        List<ReservationResponse> responseList =
                pageContent
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        // =================================================
        // RETURN PAGE
        // =================================================

        return new PageImpl<>(
                responseList,
                pageable,
                reservations.size());
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