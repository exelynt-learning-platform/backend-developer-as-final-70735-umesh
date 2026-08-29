package org.techhub.dto;

import java.time.LocalDateTime;

import org.techhub.entity.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;

    private Long userId;

    private Long resourceId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double price;

    private ReservationStatus status;

    private String purpose;
}