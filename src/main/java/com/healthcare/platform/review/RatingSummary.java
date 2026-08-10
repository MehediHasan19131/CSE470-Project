package com.healthcare.platform.review;

import java.time.LocalDateTime;

/**
 * Plain Java object for one row of the `ratings` table - the running
 * average + count for a single target, kept in sync with `reviews` by
 * {@link ReviewJdbcRepository#refreshRatingSummary(Long)} every time a
 * review is created or updated.
 */
public class RatingSummary {

    private Long targetId;
    private double averageRating;
    private int totalReviews;
    private LocalDateTime updatedAt;

    public RatingSummary() {
    }

    public RatingSummary(Long targetId, double averageRating, int totalReviews, LocalDateTime updatedAt) {
        this.targetId = targetId;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.updatedAt = updatedAt;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
