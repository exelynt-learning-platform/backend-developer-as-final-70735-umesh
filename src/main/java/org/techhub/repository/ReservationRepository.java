package org.techhub.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.techhub.entity.Reservation;
import org.techhub.entity.ReservationStatus;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    // USER - only own reservations
    List<Reservation> findByUserId(Long userId);

    // Resource reservations
    List<Reservation> findByResourceId(Long resourceId);

    // Check if reservations exist for resource
    boolean existsByResourceId(Long resourceId);

    // Check if active reservations exist for resource
    boolean existsByResourceIdAndStatusIn(Long resourceId, List<ReservationStatus> statuses);

    // Find by status
    List<Reservation> findByStatus(ReservationStatus status);

    // Check overlapping reservation
    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE r.resource.id = :resourceId
            AND r.status <> :cancelledStatus
            AND r.startTime < :endTime
            AND r.endTime > :startTime
            """)
    boolean existsOverlappingReservation(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("cancelledStatus") ReservationStatus cancelledStatus
    );

    // Filter reservations with pagination support
    @Query("""
            SELECT r FROM Reservation r
            WHERE (:status IS NULL OR r.status = :status)
            AND (:minPrice IS NULL OR r.price >= :minPrice)
            AND (:maxPrice IS NULL OR r.price <= :maxPrice)
            """)
    Page<Reservation> filterReservations(
            @Param("status") ReservationStatus status,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // Filter reservations for regular users (by their email)
    @Query("""
            SELECT r FROM Reservation r
            WHERE r.user.email = :userEmail
            AND (:status IS NULL OR r.status = :status)
            AND (:minPrice IS NULL OR r.price >= :minPrice)
            AND (:maxPrice IS NULL OR r.price <= :maxPrice)
            """)
    Page<Reservation> filterUserReservations(
            @Param("userEmail") String userEmail,
            @Param("status") ReservationStatus status,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
}