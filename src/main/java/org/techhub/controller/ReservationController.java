package org.techhub.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.techhub.dto.ReservationRequest;
import org.techhub.dto.ReservationResponse;
import org.techhub.entity.ReservationStatus;
import org.techhub.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService) {

        this.reservationService = reservationService;
    }

    // =====================================================
    // USER + ADMIN
    // CREATE RESERVATION
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        ReservationResponse response =
                reservationService.createReservation(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    // =====================================================
    // USER + ADMIN
    // GET MY RESERVATIONS
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>>
    getMyReservations() {

        return ResponseEntity.ok(
                reservationService.getMyReservations());
    }

    // =====================================================
    // USER + ADMIN
    // GET RESERVATION BY ID
    // USER -> OWN ONLY
    // ADMIN -> ANY
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse>
    getReservationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservationService
                        .getReservationById(id));
    }

    // =====================================================
    // USER
    // CANCEL OWN RESERVATION
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationResponse>
    cancelReservation(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservationService
                        .cancelReservation(id));
    }

    // =====================================================
    // ADMIN
    // GET ALL RESERVATIONS
    // =====================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<ReservationResponse>>
    getAllReservations() {

        return ResponseEntity.ok(
                reservationService
                        .getAllReservations());
    }

    // =====================================================
    // ADMIN
    // CONFIRM RESERVATION
    // =====================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{id}/confirm")
    public ResponseEntity<ReservationResponse>
    confirmReservation(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservationService
                        .confirmReservation(id));
    }

    // =====================================================
    // ADMIN + USER
    // FILTER RESERVATIONS WITH PAGINATION
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/filter")
    public ResponseEntity<Page<ReservationResponse>>
    filterReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {

        // Build Pageable with sorting
        Sort.Direction direction = Sort.Direction.ASC;

        if (sortOrder != null
                && sortOrder.equalsIgnoreCase("desc")) {

            direction = Sort.Direction.DESC;
        }

        String sortField = sortBy != null ? sortBy : "id";

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, sortField)
        );

        Page<ReservationResponse> filteredReservations =
                reservationService.filterReservations(
                        status,
                        minPrice,
                        maxPrice,
                        pageable
                );

        return ResponseEntity.ok(
                filteredReservations);
    }
}