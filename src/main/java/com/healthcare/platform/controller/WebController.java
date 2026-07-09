package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.DashboardService;
import com.healthcare.platform.service.ListingService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    private final CurrentUserService currentUserService;
    private final DashboardService dashboardService;
    private final ListingService listingService;
    private final MedicineRepository medicines;

    public WebController(
            CurrentUserService currentUserService,
            DashboardService dashboardService,
            ListingService listingService,
            MedicineRepository medicines
    ) {
        this.currentUserService = currentUserService;
        this.dashboardService = dashboardService;
        this.listingService = listingService;
        this.medicines = medicines;
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

        if (user.getRole() == UserRole.PATIENT) {
            model.addAttribute("doctors", listingService.doctors(null, null));
            model.addAttribute("hospitals", listingService.hospitals(null));
            model.addAttribute("pharmacies", listingService.pharmacies(null, null));
        }

        return dashboardTemplate(user.getRole());
    }

    @GetMapping("/logged-out")
    public String loggedOut() {
        return "logged-out";
    }

    @PostMapping("/pharmacy/medicines/{medicineId}/sell")
    public String sellMedicine(@PathVariable Long medicineId) {
        medicines.findById(medicineId).ifPresent(medicine -> {
            if (medicine.getStockQuantity() > 0) {
                medicine.setStockQuantity(medicine.getStockQuantity() - 1);
                medicines.save(medicine);
            }
        });
        return "redirect:/dashboard";
    }

    private String dashboardTemplate(UserRole role) {
        return switch (role) {
            case ADMIN -> "dashboard-admin";
            case PATIENT -> "dashboard-patient";
            case DOCTOR -> "dashboard-doctor";
            case HOSPITAL -> "dashboard-hospital";
            case PHARMACY -> "dashboard-pharmacy";
            case DIAGNOSTIC, AMBULANCE -> "dashboard-unavailable";
        };
    }
}
