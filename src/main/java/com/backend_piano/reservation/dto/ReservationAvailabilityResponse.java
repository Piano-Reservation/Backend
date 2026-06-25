package com.backend_piano.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record ReservationAvailabilityResponse(
        @Schema(description = "연습실 ID", example = "2") Long roomId,
        @Schema(description = "연습실 이름", example = "예술체육대학2-301호") String roomName,
        @Schema(description = "층", example = "3") int floor,
        @Schema(description = "조회 날짜", example = "2026-06-25") LocalDate date,
        @Schema(description = "시간 슬롯 목록") List<ReservationAvailabilitySlotResponse> slots
) {
    public static ReservationAvailabilityResponse of(
            Long roomId,
            String roomName,
            int floor,
            LocalDate date,
            List<ReservationAvailabilitySlotResponse> slots
    ) {
        return new ReservationAvailabilityResponse(roomId, roomName, floor, date, slots);
    }
}
