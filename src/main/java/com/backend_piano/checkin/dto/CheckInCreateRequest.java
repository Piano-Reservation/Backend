package com.backend_piano.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckInCreateRequest(
        @Schema(description = "연습실 QR 토큰", example = "ROOM_301_QR_TOKEN")
        @NotBlank
        @Size(max = 100) String qrToken
) {
}
