package com.backend_piano.notification.controller;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.dto.ApiResult;
import com.backend_piano.global.event.NotificationEvent;
import com.backend_piano.notification.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: 운영 배포 전 반드시 제거
@RestController
@RequestMapping("/api/test/notifications")
@RequiredArgsConstructor
class NotificationTestController {

    private final ApplicationEventPublisher eventPublisher;

    // SSE 실시간 알림 수신 테스트용 — 로그인한 사용자에게 알림 이벤트를 직접 발행한다.
    // 테스트용이라서 controller 에서 바로 비지니스로직을 호출함 (레이어 위반함)
    @Transactional
    @PostMapping
    public ApiResult<Void> publish(
            @AuthenticationPrincipal StudentDetails studentDetails,
            @RequestParam NotificationType type,
            @RequestParam String message) {
        eventPublisher.publishEvent(
                NotificationEvent.of(studentDetails.getStudent(), type, message));
        return ApiResult.ok(null);
    }
}
