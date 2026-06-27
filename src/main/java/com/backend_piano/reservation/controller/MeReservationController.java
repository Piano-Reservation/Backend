package com.backend_piano.reservation.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.reservation.dto.MyReservationResponse;
import com.backend_piano.reservation.service.MeReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "내 예약")
@Validated
@RestController
@RequestMapping("/api/me/reservations")
@RequiredArgsConstructor
public class MeReservationController {

    private final MeReservationService meReservationService;

    @Operation(summary = "내 예약 목록 조회", description = "특정 날짜의 내 예약 목록을 시작 시간순으로 조회합니다.")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping
    public ApiResult<List<MyReservationResponse>> getMyReservations(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @Parameter(description = "조회 날짜", example = "2026-06-26")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResult.ok(meReservationService.getMyReservations(studentDetails, date));
    }

    @Operation(summary = "지난 이용 내역 조회", description = "오늘 이전의 내 예약 이력을 최신순으로 페이지 조회합니다.")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping("/history")
    public ApiResult<Page<MyReservationResponse>> getMyReservationHistory(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(value = 100, message = "페이지 사이즈는 최대 100까지 허용합니다.") int size) {
        return ApiResult.ok(meReservationService.getMyReservationHistory(studentDetails, page, size));
    }
}
