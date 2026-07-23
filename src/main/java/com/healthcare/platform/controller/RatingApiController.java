package com.healthcare.platform.controller;

import com.healthcare.platform.dto.RatingRequest;
import com.healthcare.platform.dto.RatingResponse;
import com.healthcare.platform.dto.RatingSummaryResponse;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.RatingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Sprint 2 — Ratings / Reviews about doctors, hospitals, ambulance, and pharmacy. */
@RestController
public class RatingApiController {
    private final RatingService ratingService;
    private final CurrentUserService currentUserService;

    public RatingApiController(RatingService ratingService, CurrentUserService currentUserService) {
        this.ratingService = ratingService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/api/ratings")
    public RatingResponse submit(@Valid @RequestBody RatingRequest body, Authentication authentication) {
        User reviewer = currentUserService.get(authentication);
        return ratingService.submit(reviewer, body);
    }

    @GetMapping("/api/ratings/target/{targetUserId}")
    public RatingSummaryResponse forTarget(@PathVariable Long targetUserId) {
        return ratingService.forTarget(targetUserId);
    }

    @GetMapping("/api/ratings/me")
    public List<RatingResponse> myReviews(Authentication authentication) {
        return ratingService.myReviews(currentUserService.get(authentication));
    }
}
