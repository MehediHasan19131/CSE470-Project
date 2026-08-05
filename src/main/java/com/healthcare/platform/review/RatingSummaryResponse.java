package com.healthcare.platform.review;

public record RatingSummaryResponse(
        Long targetId,
        double averageRating,
        int totalReviews
) {
    public static RatingSummaryResponse from(RatingSummary summary) {
        return new RatingSummaryResponse(summary.getTargetId(), summary.getAverageRating(), summary.getTotalReviews());
    }
}
