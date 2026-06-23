package com.backend_piano.notification.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.notification.model.NotificationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: 운영 배포 전 반드시 제거
@Tag(name = "[테스트] 알림")
@RestController
@RequestMapping("/api/test/notifications")
@RequiredArgsConstructor
class NotificationTestController {

    private final NotificationTestService notificationTestService;

    @Operation(summary = "테스트 알림 발행", description = "로그인한 사용자에게 알림 이벤트를 직접 발행합니다. local 환경 전용.")
    @ApiResponse(responseCode = "401", description = "미인증")
    @PostMapping
    public ApiResult<Void> publish(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @Parameter(description = """
                    알림 유형

                    | 값 | 설명 |
                    |---|---|
                    | RESERVATION_CREATED | 예약 생성 성공 |
                    | RESERVATION_CANCELLED | 예약 취소 (본인 취소 또는 자동 NO_SHOW) |
                    | CHECK_IN_COMPLETED | QR 체크인 성공 |
                    | NO_SHOW_MARKED | 자동 미입실 처리 |
                    | RESTRICTION_APPLIED | 이용 제한 시작 |
                    | RESTRICTION_LIFTED | 이용 제한 종료 |
                    | BASEMENT_LONG_STAY | 지하 미퇴실 4시간 초과 |
                    """)
            @RequestParam NotificationType type,
            @RequestParam(defaultValue = "테스트 알림입니다.") String message) {
        notificationTestService.publishEvent(studentDetails.getStudent(), type, message);
        return ApiResult.ok(null);
    }
}
