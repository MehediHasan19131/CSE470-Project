package com.healthcare.platform.dto;

import com.healthcare.platform.model.Rating;
import java.time.LocalDateTime;

public record RatingResponse(
        Long id,
        Long targetUserId,
        String targetName,
        Long reviewerId,
        String reviewerName,
        int score,
        String comment,
        LocalDateTime createdAt
) {
    public static RatingResponse from(Rating rating) {
        return new RatingResponse(
                rating.getId() == null ? null : rating.getId(),
                rating.getTargetUser().getId(),
                rating.getTargetUser().getFullName(),
                rating.getReviewerUser() == null ? null : rating.getReviewerUser().getId(),
                rating.getReviewerUser() == null ? "Anonymous" : rating.getReviewerUser().getFullName(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }
}
