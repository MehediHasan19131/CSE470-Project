// SPRINT 3 MERGE NOTE:
// This file REPLACES the Sprint 2 version of WebController.java.
// Changes: 2 new page routes were added for Telemedicine — /telemedicine/history and
// /telemedicine/call/{consultationId}. Everything else is unchanged from Sprint 2.
package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.DashboardService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    private final CurrentUserService currentUserService;
    private final DashboardService dashboardService;

    public WebController(CurrentUserService currentUserService, DashboardService dashboardService) {
        this.currentUserService = currentUserService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String login(@RequestParam(required = false) String error, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("error", error);
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("data", dashboardService.dashboard(user));
        return dashboardTemplate(user.getRole());
    }

    @GetMapping("/logged-out")
    public String loggedOut() {
        return "logged-out";
    }

    // ---- Sprint 2 feature pages ----

    @GetMapping("/appointments/book")
    public String appointmentBooking(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "appointment-booking";
    }

    @GetMapping("/appointments/manage")
    public String appointmentManagement(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "appointment-management";
    }

    @GetMapping("/ambulance/book")
    public String ambulanceBooking(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "ambulance-booking";
    }

    @GetMapping("/medicines/order")
    public String medicineOrdering(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "medicine-ordering";
    }

    @GetMapping("/orders/manage")
    public String orderManagement(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "order-management";
    }

    @GetMapping("/reviews")
    public String reviews(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "reviews";
    }

    // ---- Sprint 3 feature pages (Telemedicine) ----

    @GetMapping("/telemedicine/history")
    public String consultationHistory(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "consultation-history";
    }

    @GetMapping("/telemedicine/call/{consultationId}")
    public String videoCall(@PathVariable Long consultationId, Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        model.addAttribute("consultationId", consultationId);
        return "video-call";
    }

    private String dashboardTemplate(UserRole role) {
        return switch (role) {
            case ADMIN -> "dashboard-admin";
            case PATIENT -> "dashboard-patient";
            case DOCTOR -> "dashboard-doctor";
            case HOSPITAL -> "dashboard-hospital";
            case PHARMACY -> "dashboard-pharmacy";
            case AMBULANCE -> "dashboard-ambulance";
            case DIAGNOSTIC -> "dashboard-unavailable";
        };
    }
}
