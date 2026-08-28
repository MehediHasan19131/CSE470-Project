package com.healthcare.platform.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/** What we receive from {@code PUT /api/reviews/{id}}. */
public record ReviewUpdateRequest(
        @Min(1) @Max(5) int rating,
        @Size(max = 1000, message = "Comment can't be longer than 1000 characters") String comment
) {
}
