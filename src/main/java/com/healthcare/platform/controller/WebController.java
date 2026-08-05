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

    // ---- Sprint 2 feature pages (Mehedi Hasan) ----
    // Namespaced under /mehedi because Rony's AppointmentController already owns
    // /appointments/book and Nahian's ReviewWebController already owns /reviews;
    // identical patterns make Spring fail at startup with Ambiguous mapping.

    @GetMapping("/mehedi/appointments/book")
    public String appointmentBooking(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "appointment-booking";
    }

    @GetMapping("/mehedi/appointments/manage")
    public String appointmentManagement(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "appointment-management";
    }

    @GetMapping("/ambulance/book")
    public String ambulanceBooking(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "ambulance-booking";
    }

    @GetMapping("/mehedi/medicines/order")
    public String medicineOrdering(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "medicine-ordering";
    }

    @GetMapping("/mehedi/orders/manage")
    public String orderManagement(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "order-management";
    }

    @GetMapping("/mehedi/reviews")
    public String reviews(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        return "reviews";
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
