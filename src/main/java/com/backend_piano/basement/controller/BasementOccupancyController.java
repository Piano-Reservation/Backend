package com.backend_piano.basement.controller;

import com.backend_piano.basement.dto.BasementOccupancyStatusResponse;
import com.backend_piano.basement.service.BasementOccupancyQueryService;
import com.backend_piano.global.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지하 연습실")
@RestController
@RequestMapping("/api/basement/occupancies")
@RequiredArgsConstructor
public class BasementOccupancyController {

    private final BasementOccupancyQueryService basementOccupancyQueryService;

    @Operation(summary = "지하 연습실 현재 점유 현황 조회")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping("/status")
    public ApiResult<List<BasementOccupancyStatusResponse>> getCurrentOccupancies() {
        return ApiResult.ok(basementOccupancyQueryService.getCurrentOccupancies());
    }
}
