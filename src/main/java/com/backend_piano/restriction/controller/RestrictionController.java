package com.backend_piano.restriction.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.restriction.dto.RestrictionResponse;
import com.backend_piano.restriction.service.RestrictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이용 제한")
@RestController
@RequestMapping("/api/me/restrictions")
@RequiredArgsConstructor
public class RestrictionController {

    private final RestrictionService restrictionService;

    @Operation(summary = "내 이용 제한 상태 조회")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping("/current")
    public ApiResult<RestrictionResponse> getCurrentRestriction(
            @AuthenticationPrincipal StudentDetails studentDetails) {
        return ApiResult.ok(restrictionService.getCurrentRestriction(studentDetails));
    }
}
