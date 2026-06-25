package com.backend_piano.reservation.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.reservation.dto.ReservationCreateRequest;
import com.backend_piano.reservation.dto.ReservationAvailabilityResponse;
import com.backend_piano.reservation.dto.ReservationResponse;
import com.backend_piano.reservation.service.ReservationService;
import com.backend_piano.reservation.service.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "예약")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationQueryService reservationQueryService;
    private final ReservationService reservationService;

    @Operation(summary = "연습실 예약 가능 시간 조회")
    @ApiResponse(responseCode = "404", description = "연습실 없음")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping("/availability")
    public ApiResult<ReservationAvailabilityResponse> getAvailability(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @Parameter(description = "연습실 ID", example = "2")
            @RequestParam Long roomId,
            @Parameter(description = "조회 날짜", example = "2026-06-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResult.ok(reservationQueryService.getAvailability(studentDetails, roomId, date));
    }

    @Operation(summary = "연습실 예약 생성")
    @ApiResponse(responseCode = "400", description = "예약 생성 규칙 위반")
    @ApiResponse(responseCode = "401", description = "미인증")
    @ApiResponse(responseCode = "403", description = "이용 제한으로 예약 불가")
    @ApiResponse(responseCode = "404", description = "연습실 없음")
    @ApiResponse(responseCode = "409", description = "시간 충돌")
    @PostMapping
    public ApiResult<ReservationResponse> createReservation(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestBody @Valid ReservationCreateRequest request) {
        return ApiResult.ok(reservationService.createReservation(studentDetails, request));
    }
}
