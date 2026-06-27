package com.backend_piano.basement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record BasementOccupancyStatusResponse(
        @Schema(description = "연습실 ID", example = "8") Long roomId,
        @Schema(description = "층", example = "0") int floor,
        @Schema(description = "호실 코드", example = "B115") String roomCode,
        @Schema(description = "연습실 이름", example = "예술체육대학2-B115호") String roomName,
        @Schema(description = "현재 사용 중 여부", example = "true") boolean occupied,
        @Schema(description = "현재 사용자 이름", example = "김테스트") String occupantName,
        @Schema(description = "입실 기록 ID", example = "31") Long occupancyId,
        @Schema(description = "입실 시각", example = "2026-06-27T17:00:00") LocalDateTime enteredAt
) {
    public static BasementOccupancyStatusResponse vacant(
            Long roomId,
            int floor,
            String roomCode,
            String roomName
    ) {
        return new BasementOccupancyStatusResponse(
                roomId,
                floor,
                roomCode,
                roomName,
                false,
                null,
                null,
                null);
    }

    public static BasementOccupancyStatusResponse occupied(
            Long roomId,
            int floor,
            String roomCode,
            String roomName,
            String occupantName,
            Long occupancyId,
            LocalDateTime enteredAt
    ) {
        return new BasementOccupancyStatusResponse(
                roomId,
                floor,
                roomCode,
                roomName,
                true,
                occupantName,
                occupancyId,
                enteredAt
        );
    }
}
