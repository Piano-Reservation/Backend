package com.backend_piano.room.controller;

import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.room.dto.RoomResponse;
import com.backend_piano.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "연습실")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "층별 연습실 목록 조회")
    @ApiResponse(responseCode = "400", description = "잘못된 층 정보")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping
    public ApiResult<List<RoomResponse>> getRooms(
            @Parameter(description = "조회할 층", example = "3")
            @RequestParam int floor) {
        return ApiResult.ok(roomService.getRooms(floor));
    }
}
