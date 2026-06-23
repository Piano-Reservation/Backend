package com.backend_piano.notification.dto;

import com.backend_piano.notification.model.Notification;
import com.backend_piano.notification.model.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record NotificationResponse(
        @Schema(description = "알림 ID", example = "1") Long id,
        @Schema(description = "알림 유형", example = "RESERVATION_CREATED") NotificationType type,
        @Schema(description = "알림 내용", example = "예약이 성공적으로 생성되었습니다.") String message,
        @JsonProperty("isRead") @Schema(description = "읽음 여부", example = "false") boolean isRead,
        @Schema(description = "생성 일시", example = "2026-06-23T10:00:00") LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
