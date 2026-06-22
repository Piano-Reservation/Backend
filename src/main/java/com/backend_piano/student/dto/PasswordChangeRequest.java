package com.backend_piano.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @Schema(example = "000000") @NotBlank String currentPassword,
        @Schema(example = "password1") @NotBlank @Size(min = 6, max = 72) String newPassword
) {}
