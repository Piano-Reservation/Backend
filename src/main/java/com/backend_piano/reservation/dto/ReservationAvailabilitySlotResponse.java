package com.backend_piano.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;

public record ReservationAvailabilitySlotResponse(
        @Schema(description = "시작 시간", example = "09:00:00") LocalTime startTime,
        @Schema(description = "종료 시간", example = "10:00:00") LocalTime endTime,
        @Schema(description = "슬롯 상태", example = "AVAILABLE") AvailabilitySlotStatus status,
        @Schema(description = "내 예약일 경우 예약 ID", example = "31") Long reservationId
) {
    public static ReservationAvailabilitySlotResponse of(
            LocalTime startTime,
            LocalTime endTime,
            AvailabilitySlotStatus status,
            Long reservationId
    ) {
        return new ReservationAvailabilitySlotResponse(startTime, endTime, status, reservationId);
    }
}
