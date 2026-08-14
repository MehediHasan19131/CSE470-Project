package com.healthcare.platform.review;

import java.time.LocalDateTime;

/**
 * Plain Java object for one row of the `reviews` table, used ONLY by
 * {@link ReviewJdbcRepository}. No JPA @Entity here either - same "no ORM"
 * rule Member 1 used for {@code AuthUser}, applied to this sprint's tables.
 *
 * {@code reviewerName} is not a column on `reviews` - it's filled in by
 * {@link ReviewJdbcRepository} whenever a query joins against `users`, purely
 * so templates/JSON responses can show a name instead of a raw id. It's left
 * {@code null} for queries that don't need it (e.g. the duplicate-review check).
 */
public class Review {

    private Long id;
    private Long reviewerId;
    private String reviewerName;
    private Long targetId;
    private String targetName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Review() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
