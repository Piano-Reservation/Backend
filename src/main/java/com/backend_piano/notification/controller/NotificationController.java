package com.backend_piano.notification.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.notification.dto.NotificationResponse;
import com.backend_piano.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "내 알림 목록 조회", description = "최신순으로 정렬된 내 알림 목록을 반환합니다.")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping
    public ApiResult<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal StudentDetails studentDetails) {
        return ApiResult.ok(notificationService.getMyNotifications(studentDetails));
    }
}
