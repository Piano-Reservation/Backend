package com.backend_piano.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationUpdateRequest(
        @JsonProperty("isRead") @Schema(description = "읽음 여부", example = "true") boolean isRead
) {
}
