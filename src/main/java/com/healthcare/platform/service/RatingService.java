package com.healthcare.platform.service;

import com.healthcare.platform.dto.RatingRequest;
import com.healthcare.platform.dto.RatingResponse;
import com.healthcare.platform.dto.RatingSummaryResponse;
import com.healthcare.platform.model.Rating;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Sprint 2 — Ratings / Reviews for doctors, hospitals, pharmacies, and ambulance providers. */
@Service
public class RatingService {
    private final RatingRepository ratings;
    private final UserRepository users;

    public RatingService(RatingRepository ratings, UserRepository users) {
        this.ratings = ratings;
        this.users = users;
    }

    public RatingResponse submit(User reviewer, RatingRequest request) {
        User target = users.findById(request.targetUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reviewed user not found"));
        if (target.getId().equals(reviewer.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot review yourself");
        }
        if (target.getRole() == UserRole.PATIENT || target.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only doctors, hospitals, pharmacies, and ambulance providers can be reviewed");
        }
        Rating rating = new Rating(target, reviewer, request.score(), request.comment());
        return RatingResponse.from(ratings.save(rating));
    }

    public RatingSummaryResponse forTarget(Long targetUserId) {
        User target = users.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<RatingResponse> reviews = ratings.findByTargetUserIdOrderByCreatedAtDesc(targetUserId).stream()
                .map(RatingResponse::from)
                .toList();
        double average = ratings.averageScore(targetUserId);
        long total = ratings.countByTargetUserId(targetUserId);
        return new RatingSummaryResponse(target.getId(), target.getFullName(), Math.round(average * 10.0) / 10.0, total, reviews);
    }

    public List<RatingResponse> myReviews(User reviewer) {
        return ratings.findAll().stream()
                .filter(r -> r.getReviewerUser() != null && r.getReviewerUser().getId().equals(reviewer.getId()))
                .map(RatingResponse::from)
                .toList();
    }
}
