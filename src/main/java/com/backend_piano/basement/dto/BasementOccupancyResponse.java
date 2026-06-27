package com.backend_piano.basement.dto;

import com.backend_piano.basement.model.BasementOccupancy;
import com.backend_piano.basement.model.BasementOccupancyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record BasementOccupancyResponse(
        @Schema(description = "입실 기록 ID", example = "1") Long occupancyId,
        @Schema(description = "연습실 ID", example = "8") Long roomId,
        @Schema(description = "층", example = "0") int floor,
        @Schema(description = "호실 코드", example = "B115") String roomCode,
        @Schema(description = "연습실 이름", example = "예술체육대학2-B115호") String roomName,
        @Schema(description = "입실 상태", example = "IN_USE") BasementOccupancyStatus status,
        @Schema(description = "입실 시각", example = "2026-06-27T17:00:00") LocalDateTime enteredAt,
        @Schema(description = "퇴실 시각", example = "2026-06-27T19:00:00") LocalDateTime exitedAt
) {
    public static BasementOccupancyResponse from(BasementOccupancy occupancy) {
        return new BasementOccupancyResponse(
                occupancy.getId(),
                occupancy.getRoom().getId(),
                occupancy.getRoom().getFloor().level(),
                occupancy.getRoom().getCode(),
                occupancy.getRoom().getName(),
                occupancy.getStatus(),
                occupancy.getEnteredAt(),
                occupancy.getExitedAt()
        );
    }
}
