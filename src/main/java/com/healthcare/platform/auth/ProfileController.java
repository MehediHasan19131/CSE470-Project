package com.healthcare.platform.auth;

import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.review.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Own-account profile page. Also the landing page after login, so this is
 * where each role's "dashboard" panel lives - see addRoleDashboardData(...)
 * below. Now depends on ReviewService (review package) in addition to
 * AuthUserJdbcRepository, purely to show review data relevant to whoever's
 * logged in - doesn't touch review data, only reads it.
 */
@Controller
public class ProfileController {

    private final AuthUserJdbcRepository authUsers;
    private final ReviewService reviewService;

    public ProfileController(AuthUserJdbcRepository authUsers, ReviewService reviewService) {
        this.authUsers = authUsers;
        this.reviewService = reviewService;
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        AuthUser user = authUsers.findByEmail(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
        addRoleDashboardData(user, model);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Authentication authentication,
                                 @RequestParam String fullName,
                                 @RequestParam(required = false) String phone,
                                 Model model) {
        AuthUser current = authUsers.findByEmail(authentication.getName()).orElseThrow();
        authUsers.updateProfile(current.getId(), fullName, phone);

        AuthUser updated = authUsers.findById(current.getId()).orElseThrow();
        model.addAttribute("user", updated);
        model.addAttribute("updated", true);
        addRoleDashboardData(updated, model);
        return "profile";
    }

    /**
     * PATIENT -> the reviews they've written.
     * Any provider role (DOCTOR/HOSPITAL/PHARMACY/DIAGNOSTIC/AMBULANCE) -> reviews written about them.
     * ADMIN -> neither; profile.html shows an "Open admin panel" link instead.
     */
    private void addRoleDashboardData(AuthUser user, Model model) {
        if (user.getRole() == UserRole.PATIENT) {
            model.addAttribute("myReviews", reviewService.getReviewsByReviewer(user.getId()));
        } else if (user.getRole() != UserRole.ADMIN) {
            model.addAttribute("ratingSummary", reviewService.getRatingSummary(user.getId()));
            model.addAttribute("reviewsAboutMe", reviewService.getReviewsForTarget(user.getId()));
        }
    }
}
