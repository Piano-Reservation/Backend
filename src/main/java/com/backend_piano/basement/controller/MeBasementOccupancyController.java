package com.backend_piano.basement.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.basement.dto.BasementOccupancyResponse;
import com.backend_piano.basement.service.MeBasementOccupancyService;
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

@Tag(name = "내 지하 연습실 기록")
@RestController
@RequestMapping("/api/me/basement/occupancies")
@RequiredArgsConstructor
public class MeBasementOccupancyController {

    private final MeBasementOccupancyService meBasementOccupancyService;

    @Operation(summary = "내 지하 연습실 입실 기록 조회")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping
    public ApiResult<List<BasementOccupancyResponse>> getMyOccupancies(
            @AuthenticationPrincipal StudentDetails studentDetails) {
        return ApiResult.ok(meBasementOccupancyService.getMyOccupancies(studentDetails));
    }
}
