package com.healthcare.platform.auth;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    private final AuthUserJdbcRepository authUsers;

    public ProfileController(AuthUserJdbcRepository authUsers) {
        this.authUsers = authUsers;
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        AuthUser user = authUsers.findByEmail(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
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
        return "profile";
    }
}
