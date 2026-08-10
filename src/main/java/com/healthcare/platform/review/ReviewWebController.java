package com.healthcare.platform.review;

import com.healthcare.platform.auth.AuthUser;
import com.healthcare.platform.auth.AuthUserJdbcRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Server-rendered pages for the Review & Rating System (Member 2, Sprint
 * task: Review Form, Rating Display).
 * <p>
 * {@code GET/POST /reviews/{targetId}} is one page holding both frontend
 * pieces at once - a "Rating Display" panel (average + review list) and a
 * "Review Form" panel underneath it - the same way {@code profile.html}
 * combines an "Account" panel and a "Status" panel on one page.
 * <p>
 * {@code GET /reviews} is an extra directory/browse page, not one of the two
 * assigned frontend items - it exists only so there's a way to reach a
 * provider's review page without hard-coding an id in the URL (provider
 * search/listing is a different member's task). See README.
 */
@Controller
public class ReviewWebController {

    private final ReviewService reviewService;
    private final AuthUserJdbcRepository authUsers;

    public ReviewWebController(ReviewService reviewService, AuthUserJdbcRepository authUsers) {
        this.reviewService = reviewService;
        this.authUsers = authUsers;
    }

    @GetMapping("/reviews")
    public String directory(Authentication authentication, Model model) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        model.addAttribute("user", me);
        model.addAttribute("providers", reviewService.getProviders());
        return "reviews-directory";
    }

    @GetMapping("/reviews/{targetId}")
    public String show(@PathVariable Long targetId, Authentication authentication, Model model) {
        AuthUser target = authUsers.findById(targetId).orElse(null);
        if (target == null) {
            return "redirect:/reviews?error=Provider not found";
        }

        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        loadPage(target, me, model);
        return "review-target";
    }

    @PostMapping("/reviews/{targetId}")
    public String submit(@PathVariable Long targetId,
                          @RequestParam int rating,
                          @RequestParam(required = false) String comment,
                          Authentication authentication,
                          Model model) {
        AuthUser target = authUsers.findById(targetId).orElse(null);
        if (target == null) {
            return "redirect:/reviews?error=Provider not found";
        }

        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();

        try {
            var existing = reviewService.getMyReview(me.getId(), targetId);
            if (existing.isPresent()) {
                reviewService.updateReview(existing.get().getId(), me.getId(), rating, comment);
            } else {
                reviewService.createReview(me.getId(), targetId, rating, comment);
            }
            model.addAttribute("saved", true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }

        loadPage(target, me, model);
        return "review-target";
    }

    private void loadPage(AuthUser target, AuthUser me, Model model) {
        model.addAttribute("me", me);
        model.addAttribute("user", me);
        model.addAttribute("target", target);
        model.addAttribute("summary", reviewService.getRatingSummary(target.getId()));
        model.addAttribute("reviews", reviewService.getReviewsForTarget(target.getId()));
        model.addAttribute("myReview", reviewService.getMyReview(me.getId(), target.getId()).orElse(null));
    }
}
