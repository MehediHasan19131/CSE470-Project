package com.healthcare.platform.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** What we receive from {@code POST /api/reviews}. */
public record ReviewCreateRequest(
        @NotNull Long targetId,
        @Min(1) @Max(5) int rating,
        @Size(max = 1000, message = "Comment can't be longer than 1000 characters") String comment
) {
}
