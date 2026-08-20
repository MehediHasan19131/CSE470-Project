package com.healthcare.platform.auth;

import com.healthcare.platform.model.UserRole;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public String showForm(@RequestParam(required = false) String role, Model model) {
        if (!model.containsAttribute("registerRequest")) {
            RegisterRequest request = new RegisterRequest();
            // Pre-select the role when the visitor arrived from a "Join as ..." link
            // (e.g. /register?role=DOCTOR). Unknown values are ignored so a hand-typed
            // query string can never 400 the page.
            if (role != null && !role.isBlank()) {
                try {
                    request.setRole(UserRole.valueOf(role.trim().toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                    // leave role unselected
                }
            }
            model.addAttribute("registerRequest", request);
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            registrationService.register(registerRequest);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        if (RegistrationService.requiresApproval(registerRequest.getRole())) {
            return "redirect:/?registered&pending=true";
        }
        return "redirect:/?registered";
    }
}
