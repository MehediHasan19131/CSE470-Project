package com.healthcare.platform.dto;

import java.util.List;

public record RatingSummaryResponse(
        Long targetUserId,
        String targetName,
        double averageScore,
        long totalReviews,
        List<RatingResponse> reviews
) {
}
