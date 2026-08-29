package org.techhub.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.techhub.dto.ReservationRequest;
import org.techhub.dto.ReservationResponse;
import org.techhub.entity.ReservationStatus;

public interface ReservationService {

    // USER
    ReservationResponse createReservation(
            ReservationRequest request);

    // USER - own reservations
    List<ReservationResponse> getMyReservations();

    // USER - own reservation / ADMIN - any reservation
    ReservationResponse getReservationById(Long id);

    // USER - own reservation
    ReservationResponse cancelReservation(Long id);

    // ADMIN
    List<ReservationResponse> getAllReservations();

    // ADMIN
    ReservationResponse confirmReservation(Long id);

    // FILTER + PAGINATION + SORTING
    Page<ReservationResponse> filterReservations(
            ReservationStatus status,
            Double minPrice,
            Double maxPrice,
            Pageable pageable);
}