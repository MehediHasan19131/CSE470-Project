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
    public String login(@RequestParam(required = false) String error,
                         @RequestParam(required = false) String registered,
                         @RequestParam(required = false) String pending,
                         Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("error", error);
        model.addAttribute("registered", registered != null);
        model.addAttribute("pending", pending != null);
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

    // Ambulance Module (Sprint 2 - Mehedi Hasan). The appointment, medicine-order
    // and review pages that used to live here were duplicates of Rony's, Sami's
    // and Nahian's modules and have been removed; those features are served by
    // AppointmentController, PharmacyStoreWebController and ReviewWebController.

    @GetMapping("/ambulance/book")
    public String ambulanceBooking(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "ambulance-booking";
    }

    // Telemedicine Module (Sprint 3 - Mehedi Hasan). Only these two routes were
    // taken from the Sprint 3 WebController; the rest of that file duplicated
    // Rony's /appointments/book and Nahian's /reviews, which Spring rejects as
    // Ambiguous mapping.

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

    // AI Chat / Symptom Checker Module (Sprint 4 - Mehedi Hasan).

    @GetMapping("/ai/chat")
    public String aiChat(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "ai-chat";
    }

    private String dashboardTemplate(UserRole role) {
        return switch (role) {
            case ADMIN -> "dashboard-admin";
            case PATIENT -> "dashboard-patient";
            case DOCTOR -> "dashboard-doctor";
            case HOSPITAL -> "dashboard-hospital";
            case PHARMACY -> "dashboard-pharmacy";
            case AMBULANCE -> "dashboard-ambulance";
            case DIAGNOSTIC -> "dashboard-diagnostic";
        };
    }
}
