package com.healthcare.platform.controller;

import com.healthcare.platform.service.FaqService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Public, no-login-required informational pages (FAQ, and the public "find care"
 * map lives in {@link MapController}). Kept separate from {@link WebController}
 * so these routes never touch the logged-in-user lookup.
 */
@Controller
public class PublicController {
    private final FaqService faqService;

    public PublicController(FaqService faqService) { this.faqService = faqService; }

    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("faqs", faqService.publicFaqs());
        return "faq";
    }
}
