package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.service.AmbulanceService;
import com.healthcare.platform.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Server-rendered "My ambulance requests" page for patients — lists their
 * ambulance bookings with fare and a "Pay fare" action that runs through the
 * bKash/Bank checkout.
 */
@Controller
public class AmbulanceWebController {

    private final AmbulanceService ambulanceService;
    private final CurrentUserService currentUserService;

    public AmbulanceWebController(AmbulanceService ambulanceService, CurrentUserService currentUserService) {
        this.ambulanceService = ambulanceService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/ambulance/requests")
    public String requests(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("requests", ambulanceService.myRequestEntities(user));
        return "ambulance-requests";
    }
}
