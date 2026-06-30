package com.backend_piano.checkin.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.checkin.dto.CheckInCreateRequest;
import com.backend_piano.checkin.dto.CheckInResponse;
import com.backend_piano.checkin.service.CheckInService;
import com.backend_piano.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "QR 인증")
@RestController
@RequestMapping("/api/check-ins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @Operation(summary = "QR 인증 생성")
    @ApiResponse(responseCode = "400", description = "인증 가능 시간이 아니거나 지원하지 않는 연습실")
    @ApiResponse(responseCode = "401", description = "미인증")
    @ApiResponse(responseCode = "403", description = "이용 제한으로 인증 불가")
    @ApiResponse(responseCode = "404", description = "QR 코드 또는 인증 가능한 예약 없음")
    @ApiResponse(responseCode = "409", description = "이미 인증 완료")
    @PostMapping
    public ApiResult<CheckInResponse> createCheckIn(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestBody @Valid CheckInCreateRequest request) {
        return ApiResult.ok(checkInService.createCheckIn(studentDetails, request));
    }
}
