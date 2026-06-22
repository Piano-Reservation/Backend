package com.backend_piano.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Pattern(regexp = "\\d{9}") String studentNumber,
        @NotBlank @Size(min = 8, max = 72) String password
) {}
