package com.healthcare.platform.controller.review;
import com.healthcare.platform.dto.review.*;
import com.healthcare.platform.service.review.*;

import com.healthcare.platform.model.auth.AuthUser;
import com.healthcare.platform.repository.auth.AuthUserJdbcRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * JSON API for the Review & Rating System (Member 2, Sprint task: Create
 * Review, Update Review). Session or JWT auth both work here already - this
 * controller doesn't do anything auth-specific itself, it just reads the
 * logged-in user via {@link AuthUserJdbcRepository} the same way
 * {@code AuthApiController} does. All paths below fall under the existing
 * {@code anyRequest().authenticated()} rule in SecurityConfig, so no changes
 * were needed there.
 */
@RestController
public class ReviewApiController {

    private final ReviewService reviewService;
    private final AuthUserJdbcRepository authUsers;

    public ReviewApiController(ReviewService reviewService, AuthUserJdbcRepository authUsers) {
        this.reviewService = reviewService;
        this.authUsers = authUsers;
    }

    @PostMapping("/api/reviews")
    public ResponseEntity<?> create(@Valid @RequestBody ReviewCreateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            Review saved = reviewService.createReview(me.getId(), request.targetId(), request.rating(), request.comment());
            return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.from(saved));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/api/reviews/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ReviewUpdateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            Review updated = reviewService.updateReview(id, me.getId(), request.rating(), request.comment());
            return ResponseEntity.ok(ReviewResponse.from(updated));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/api/reviews/target/{targetId}")
    public List<ReviewResponse> forTarget(@PathVariable Long targetId) {
        return reviewService.getReviewsForTarget(targetId).stream().map(ReviewResponse::from).toList();
    }

    @GetMapping("/api/ratings/{targetId}")
    public RatingSummaryResponse rating(@PathVariable Long targetId) {
        return RatingSummaryResponse.from(reviewService.getRatingSummary(targetId));
    }
}
