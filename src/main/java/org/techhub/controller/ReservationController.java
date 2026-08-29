package org.techhub.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.techhub.dto.ReservationRequest;
import org.techhub.dto.ReservationResponse;
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

    @PostMapping("/admin/{id}/confirm")
    public ResponseEntity<ReservationResponse>
    confirmReservation(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                reservationService
                        .confirmReservation(id));
    }
}