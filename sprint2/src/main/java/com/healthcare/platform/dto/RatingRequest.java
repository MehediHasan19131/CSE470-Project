package com.healthcare.platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RatingRequest(
        @NotNull Long targetUserId,
        @Min(1) @Max(5) int score,
        String comment
) {
}
