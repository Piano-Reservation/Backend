package com.backend_piano.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationUpdateRequest(
        @Schema(description = "읽음 여부", example = "true") boolean isRead
) {
}
