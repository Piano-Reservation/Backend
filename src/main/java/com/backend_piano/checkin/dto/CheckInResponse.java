package com.backend_piano.checkin.dto;

import com.backend_piano.checkin.model.CheckIn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record CheckInResponse(
        @Schema(description = "QR 인증 ID", example = "1") Long checkInId,
        @Schema(description = "예약 ID", example = "12") Long reservationId,
        @Schema(description = "연습실 ID", example = "2") Long roomId,
        @Schema(description = "층", example = "3") int floor,
        @Schema(description = "호실 코드", example = "301") String roomCode,
        @Schema(description = "연습실 이름", example = "예술체육대학2-301호") String roomName,
        @Schema(description = "예약 날짜", example = "2026-06-28") LocalDate reservationDate,
        @Schema(description = "예약 시작 시각", example = "14:00:00") LocalTime startTime,
        @Schema(description = "예약 종료 시각", example = "16:00:00") LocalTime endTime,
        @Schema(description = "QR 인증 시각", example = "2026-06-28T14:05:00") LocalDateTime checkedInAt
) {
    public static CheckInResponse from(CheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getReservation().getId(),
                checkIn.getRoom().getId(),
                checkIn.getRoom().getFloor().level(),
                checkIn.getRoom().getCode(),
                checkIn.getRoom().getName(),
                checkIn.getReservation().getReservationDate(),
                checkIn.getReservation().getStartTime(),
                checkIn.getReservation().getEndTime(),
                checkIn.getCheckedInAt()
        );
    }
}
