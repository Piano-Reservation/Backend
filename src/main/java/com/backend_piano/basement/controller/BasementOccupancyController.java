package com.backend_piano.basement.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.basement.dto.BasementOccupancyCreateRequest;
import com.backend_piano.basement.dto.BasementOccupancyResponse;
import com.backend_piano.basement.dto.BasementOccupancyStatusResponse;
import com.backend_piano.basement.service.BasementOccupancyQueryService;
import com.backend_piano.basement.service.BasementOccupancyService;
import com.backend_piano.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지하 연습실")
@RestController
@RequestMapping("/api/basement/occupancies")
@RequiredArgsConstructor
public class BasementOccupancyController {

    private final BasementOccupancyQueryService basementOccupancyQueryService;
    private final BasementOccupancyService basementOccupancyService;

    @Operation(summary = "지하 연습실 현재 점유 현황 조회")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping("/status")
    public ApiResult<List<BasementOccupancyStatusResponse>> getCurrentOccupancies() {
        return ApiResult.ok(basementOccupancyQueryService.getCurrentOccupancies());
    }

    @Operation(summary = "지하 연습실 입실 기록 생성")
    @ApiResponse(responseCode = "400", description = "잘못된 연습실 요청")
    @ApiResponse(responseCode = "401", description = "미인증")
    @ApiResponse(responseCode = "403", description = "이용 제한으로 사용 불가")
    @ApiResponse(responseCode = "409", description = "이미 점유 중이거나 중복 입실")
    @PostMapping
    public ApiResult<BasementOccupancyResponse> createOccupancy(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestBody @Valid BasementOccupancyCreateRequest request) {
        return ApiResult.ok(basementOccupancyService.createOccupancy(studentDetails, request));
    }

    @Operation(summary = "지하 연습실 퇴실 처리")
    @ApiResponse(responseCode = "200", description = "퇴실 처리 성공")
    @ApiResponse(responseCode = "401", description = "미인증")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음")
    @ApiResponse(responseCode = "404", description = "입실 기록 없음")
    @PatchMapping("/{occupancyId}")
    public ApiResult<BasementOccupancyResponse> exitOccupancy(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @PathVariable Long occupancyId) {
        return ApiResult.ok(basementOccupancyService.exitOccupancy(studentDetails, occupancyId));
    }
}
