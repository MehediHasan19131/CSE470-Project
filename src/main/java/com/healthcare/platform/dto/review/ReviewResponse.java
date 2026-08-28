package com.healthcare.platform.dto.review;
import com.healthcare.platform.model.review.*;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long reviewerId,
        String reviewerName,
        Long targetId,
        int rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getReviewerId(),
                review.getReviewerName(),
                review.getTargetId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
