package com.backend_piano.notification.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.global.sse.SseEmitterManager;
import com.backend_piano.notification.dto.NotificationResponse;
import com.backend_piano.notification.dto.NotificationUpdateRequest;
import com.backend_piano.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림")
@Validated
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterManager sseEmitterManager;

    @Operation(summary = "실시간 알림 구독", description = "SSE 연결을 맺어 실시간 알림을 수신합니다.")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal StudentDetails studentDetails) {
        return sseEmitterManager.subscribe(studentDetails.getStudent().getId());
    }

    @Operation(summary = "내 알림 목록 조회", description = "최신순으로 정렬된 내 알림 목록을 페이지 단위로 반환합니다.")
    @ApiResponse(responseCode = "401", description = "미인증")
    @GetMapping
    public ApiResult<Page<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(value = 100, message = "페이지 사이즈는 최대 100까지 허용합니다.") int size) {
        return ApiResult.ok(notificationService.getMyNotifications(studentDetails, page, size));
    }

    @Operation(summary = "알림 읽음 상태 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "401", description = "미인증"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "알림 없음")
    })
    @PatchMapping("/{notificationId}")
    public ApiResult<Void> updateReadStatus(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @PathVariable Long notificationId,
            @RequestBody NotificationUpdateRequest request) {
        notificationService.updateReadStatus(studentDetails, notificationId, request.isRead());
        return ApiResult.ok(null);
    }

    @Operation(summary = "전체 알림 읽음 상태 변경")
    @ApiResponse(responseCode = "401", description = "미인증")
    @PatchMapping
    public ApiResult<Void> updateAllReadStatus(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestBody NotificationUpdateRequest request) {
        notificationService.updateAllReadStatus(studentDetails, request.isRead());
        return ApiResult.ok(null);
    }
}
