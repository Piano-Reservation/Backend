package com.backend_piano.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationCreateRequest(
        @Schema(description = "연습실 ID", example = "2")
        @NotNull Long roomId,

        @Schema(description = "예약 날짜", example = "2026-06-25")
        @NotNull LocalDate date,

        @Schema(description = "시작 시간", example = "17:00:00")
        @NotNull LocalTime startTime,

        @Schema(description = "종료 시간", example = "19:00:00")
        @NotNull LocalTime endTime
) {}
