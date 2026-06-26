package com.backend_piano.reservation.dto;

import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record MyReservationResponse(
        @Schema(description = "예약 ID", example = "31") Long reservationId,
        @Schema(description = "날짜", example = "2026-06-26") LocalDate date,
        @Schema(description = "연습실 ID", example = "2") Long roomId,
        @Schema(description = "층", example = "3") int floor,
        @Schema(description = "호실 코드", example = "301") String roomCode,
        @Schema(description = "연습실 이름", example = "예술체육대학2-301호") String roomName,
        @Schema(description = "시작 시간", example = "14:00:00") LocalTime startTime,
        @Schema(description = "종료 시간", example = "16:00:00") LocalTime endTime,
        @Schema(description = "예약 상태", example = "RESERVED") ReservationStatus status
) {
    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getReservationDate(),
                reservation.getRoom().getId(),
                reservation.getRoom().getFloor().level(),
                reservation.getRoom().getCode(),
                reservation.getRoom().getName(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus()
        );
    }
}
