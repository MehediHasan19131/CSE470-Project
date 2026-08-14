package com.healthcare.platform.review;

import com.healthcare.platform.auth.AuthUser;
import com.healthcare.platform.auth.AuthUserJdbcRepository;
import com.healthcare.platform.model.UserRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Business rules for creating/updating reviews and keeping the `rating_summaries`
 * aggregate in sync. Reuses {@link AuthUserJdbcRepository} (read-only) to
 * check that a review target actually exists and is a provider - doesn't
 * modify it, same pattern {@code CurrentUserService} already uses.
 */
@Service
public class ReviewService {

    private final ReviewJdbcRepository reviews;
    private final AuthUserJdbcRepository authUsers;

    public ReviewService(ReviewJdbcRepository reviews, AuthUserJdbcRepository authUsers) {
        this.reviews = reviews;
        this.authUsers = authUsers;
    }

    /** Creates a new review. Fails if the reviewer already has one for this target - see {@link #updateReview}. */
    public Review createReview(Long reviewerId, Long targetId, int rating, String comment) {
        validateRating(rating);
        validateTarget(reviewerId, targetId);

        if (reviews.findByReviewerAndTarget(reviewerId, targetId).isPresent()) {
            throw new IllegalStateException("You've already reviewed this provider. Update your existing review instead.");
        }

        Review review = new Review();
        review.setReviewerId(reviewerId);
        review.setTargetId(targetId);
        review.setRating(rating);
        review.setComment(normalizeComment(comment));

        Review saved = reviews.insert(review);
        reviews.refreshRatingSummary(targetId);
        return saved;
    }

    /** Updates an existing review. Only the original reviewer may update it. */
    public Review updateReview(Long reviewId, Long requesterId, int rating, String comment) {
        validateRating(rating);

        Review existing = reviews.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("Review not found."));

        if (!existing.getReviewerId().equals(requesterId)) {
            throw new IllegalStateException("You can only update your own review.");
        }

        reviews.update(reviewId, rating, normalizeComment(comment));
        reviews.refreshRatingSummary(existing.getTargetId());
        return reviews.findById(reviewId).orElseThrow();
    }

    public List<Review> getReviewsForTarget(Long targetId) {
        return reviews.findByTarget(targetId);
    }

    /** Reviews a given user has written - powers the "Reviews you've written" panel on a patient's profile. */
    public List<Review> getReviewsByReviewer(Long reviewerId) {
        return reviews.findByReviewer(reviewerId);
    }

    public RatingSummary getRatingSummary(Long targetId) {
        return reviews.findRatingSummary(targetId)
                .orElseGet(() -> new RatingSummary(targetId, 0.0, 0, null));
    }

    public Optional<Review> getMyReview(Long reviewerId, Long targetId) {
        return reviews.findByReviewerAndTarget(reviewerId, targetId);
    }

    public List<ProviderSummary> getProviders() {
        return reviews.findProviders();
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }
    }

    private void validateTarget(Long reviewerId, Long targetId) {
        if (reviewerId.equals(targetId)) {
            throw new IllegalArgumentException("You can't review yourself.");
        }

        AuthUser target = authUsers.findById(targetId)
                .orElseThrow(() -> new NoSuchElementException("That provider doesn't exist."));

        if (target.getRole() == UserRole.PATIENT || target.getRole() == UserRole.ADMIN) {
            throw new IllegalArgumentException(
                    "You can only review service providers (doctor, hospital, pharmacy, diagnostic centre, or ambulance).");
        }
    }

    private String normalizeComment(String comment) {
        return comment == null ? null : comment.trim();
    }
}
