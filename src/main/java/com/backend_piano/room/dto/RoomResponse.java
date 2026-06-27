package com.backend_piano.room.dto;

import com.backend_piano.room.model.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RoomResponse(
        @Schema(description = "연습실 ID", example = "2") Long roomId,
        @Schema(description = "층", example = "3") int floor,
        @Schema(description = "호실 코드", example = "301") String code,
        @Schema(description = "연습실 이름", example = "예술체육대학2-301호") String name,
        @Schema(description = "전공실기 배정 대상", example = "[3,4]") List<Integer> majorPracticeTargets
) {
    public static RoomResponse of(Room room, List<Integer> majorPracticeTargets) {
        return new RoomResponse(
                room.getId(),
                room.getFloor().level(),
                room.getCode(),
                room.getName(),
                majorPracticeTargets
        );
    }
}
