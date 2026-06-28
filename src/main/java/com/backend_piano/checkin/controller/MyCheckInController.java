package com.backend_piano.checkin.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.checkin.dto.CheckInResponse;
import com.backend_piano.checkin.service.MyCheckInService;
import com.backend_piano.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "내 QR 인증")
@RestController
@RequestMapping("/api/me/check-ins")
@RequiredArgsConstructor
public class MyCheckInController {

    private final MyCheckInService myCheckInService;

    @Operation(summary = "내 QR 인증 내역 조회")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping
    public ApiResult<List<CheckInResponse>> getMyCheckIns(
            @AuthenticationPrincipal StudentDetails studentDetails) {
        return ApiResult.ok(myCheckInService.getMyCheckIns(studentDetails));
    }
}
