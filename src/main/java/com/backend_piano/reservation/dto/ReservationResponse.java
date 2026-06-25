package com.backend_piano.reservation.dto;

import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponse(
        @Schema(description = "예약 ID", example = "1") Long reservationId,
        @Schema(description = "연습실 ID", example = "2") Long roomId,
        @Schema(description = "연습실 이름", example = "예술체육대학2-301호") String roomName,
        @Schema(description = "층", example = "3") int floor,
        @Schema(description = "예약 날짜", example = "2026-06-25") LocalDate date,
        @Schema(description = "시작 시간", example = "17:00:00") LocalTime startTime,
        @Schema(description = "종료 시간", example = "19:00:00") LocalTime endTime,
        @Schema(description = "예약 상태", example = "RESERVED") ReservationStatus status
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRoom().getId(),
                reservation.getRoom().getName(),
                reservation.getRoom().getFloor().level(),
                reservation.getReservationDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus()
        );
    }
}
