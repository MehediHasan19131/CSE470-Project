package com.healthcare.platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Public, no-login-required informational pages (FAQ, and the public "find care"
 * map lives in {@link MapController}). Kept separate from {@link WebController}
 * so these routes never touch the logged-in-user lookup.
 */
@Controller
public class PublicController {

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }
}
