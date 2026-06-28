package com.backend_piano.basement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BasementOccupancyCreateRequest(
        @Schema(description = "지하 연습실 ID", example = "8")
        @NotNull @Positive Long roomId
) {}
