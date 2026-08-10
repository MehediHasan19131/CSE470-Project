package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.FacilityManagementService;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Hospital & Diagnostic Module (Member 3) - the Diagnostic Centre-owner's
 * management tool: Test offers.
 */
@Controller
public class DiagnosticManagementController {

    private final FacilityManagementService facilityManagementService;
    private final CurrentUserService currentUserService;

    public DiagnosticManagementController(FacilityManagementService facilityManagementService,
                                           CurrentUserService currentUserService) {
        this.facilityManagementService = facilityManagementService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/diagnostic/manage")
    public String manage(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("testOffers", facilityManagementService.getTestOffers(user.getId()));
        return "diagnostic-management";
    }

    @PostMapping("/diagnostic/test-offers")
    public String addTestOffer(@RequestParam String testName, @RequestParam(required = false) String description,
                                @RequestParam BigDecimal price, @RequestParam(required = false) String turnaroundTime,
                                Authentication authentication) {
        User user = currentUserService.get(authentication);
        facilityManagementService.addTestOffer(user, testName, description, price, turnaroundTime);
        return "redirect:/diagnostic/manage?testOfferSaved=true";
    }

    @PostMapping("/diagnostic/test-offers/{id}")
    public String updateTestOffer(@PathVariable Long id, @RequestParam String testName,
                                   @RequestParam(required = false) String description, @RequestParam BigDecimal price,
                                   @RequestParam(required = false) String turnaroundTime,
                                   @RequestParam(defaultValue = "false") boolean available,
                                   Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            facilityManagementService.updateTestOffer(user, id, testName, description, price, turnaroundTime, available);
            return "redirect:/diagnostic/manage?testOfferSaved=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/diagnostic/manage?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/diagnostic/test-offers/{id}/delete")
    public String deleteTestOffer(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            facilityManagementService.deleteTestOffer(user, id);
        } catch (IllegalStateException | NoSuchElementException ignored) {
        }
        return "redirect:/diagnostic/manage?testOfferDeleted=true";
    }
}
