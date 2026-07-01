package com.backend_piano.room.controller;

import com.backend_piano.basement.dto.BasementOccupancyStatusResponse;
import com.backend_piano.basement.service.BasementOccupancyQueryService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "연습실")
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final BasementOccupancyQueryService basementOccupancyQueryService;

    @Operation(summary = "층별 연습실 목록 조회")
    @ApiResponse(responseCode = "400", description = "잘못된 층 정보")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping
    public ApiResult<List<RoomResponse>> getRooms(
            @Parameter(description = "조회할 층 (지하=0, 1층=1, 3층=3)", example = "3")
            @RequestParam int floor) {
        return ApiResult.ok(roomService.getRooms(floor));
    }

    @Operation(summary = "연습실 상세 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "연습실 없음")
    @GetMapping("/{roomId}")
    public ApiResult<RoomResponse> getRoom(@PathVariable Long roomId) {
        return ApiResult.ok(roomService.getRoom(roomId));
    }

    @Operation(summary = "지하 연습실 실시간 사용 현황")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/basement/occupancy")
    public ApiResult<List<BasementOccupancyStatusResponse>> getBasementOccupancy() {
        return ApiResult.ok(basementOccupancyQueryService.getCurrentOccupancies());
    }
}
