package com.healthcare.platform.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Login page + logout landing page. Everything else that used to live here
 * (dashboard routing, pharmacy actions) belonged to other members' sprint
 * tasks and was removed from this submission.
 */
@Controller
public class WebController {

    @GetMapping("/")
    public String login(@RequestParam(required = false) String error,
                         @RequestParam(required = false) String registered,
                         Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/profile";
        }
        model.addAttribute("error", error);
        model.addAttribute("registered", registered != null);
        return "login";
    }

    @GetMapping("/logged-out")
    public String loggedOut() {
        return "logged-out";
    }
}
