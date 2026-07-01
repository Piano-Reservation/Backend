package com.backend_piano.room.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.reservation.dto.ReservationAvailabilityResponse;
import com.backend_piano.room.service.RoomScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "연습실 시간표")
@RestController
@RequestMapping("/api/room-schedules")
@RequiredArgsConstructor
public class RoomScheduleController {

    private final RoomScheduleService roomScheduleService;

    @Operation(summary = "날짜별 시간표 조회 (1·3층)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 층 정보 또는 지하층 요청")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping
    public ApiResult<List<ReservationAvailabilityResponse>> getSchedules(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @Parameter(description = "조회할 층 (1층=1, 3층=3, 지하 미지원)", example = "3")
            @RequestParam int floor,
            @Parameter(description = "조회 날짜", example = "2026-06-28")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResult.ok(roomScheduleService.getSchedulesByFloor(studentDetails, floor, date));
    }
}
