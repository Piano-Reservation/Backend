package com.backend_piano.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @Schema(example = "202334745") @NotBlank @Pattern(regexp = "\\d{9}") String studentNumber,
        @Schema(example = "020415") @NotBlank @Size(min = 6, max = 72) String password
) {}
