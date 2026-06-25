package com.backend_piano.restriction.dto;

import com.backend_piano.restriction.model.Restriction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record RestrictionResponse(
        @Schema(description = "이용 제한 여부", example = "true") boolean restricted,
        @Schema(description = "제한 시작일", example = "2026-06-20") LocalDate startDate,
        @Schema(description = "제한 종료일", example = "2026-06-27") LocalDate endDate,
        @Schema(description = "제한 사유", example = "예약 후 QR 인증 없이 미입실") String reason,
        @Schema(description = "남은 제한 일수", example = "5") long remainingDays
) {
    public static RestrictionResponse from(Restriction restriction) {
        long remaining = ChronoUnit.DAYS.between(LocalDate.now(), restriction.getEndDate());
        return new RestrictionResponse(
                remaining > 0,
                restriction.getStartDate(),
                restriction.getEndDate(),
                restriction.getReason(),
                Math.max(remaining, 0)
        );
    }

    public static RestrictionResponse none() {
        return new RestrictionResponse(false, null, null, null, 0);
    }
}
