package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.SupportService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contact-us / Report-a-problem module. Any logged-in user submits from the
 * overflow menu; admins review submissions at /admin/reports (ADMIN-only via
 * SecurityConfig's /admin/** rule).
 */
@Controller
public class SupportController {

    private final SupportService supportService;
    private final CurrentUserService currentUserService;

    public SupportController(SupportService supportService, CurrentUserService currentUserService) {
        this.supportService = supportService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/contact")
    public String contact(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        model.addAttribute("type", "CONTACT");
        return "support-form";
    }

    @GetMapping("/report")
    public String report(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        model.addAttribute("type", "REPORT");
        return "support-form";
    }

    @PostMapping("/support")
    public String submit(@RequestParam String type,
                         @RequestParam(required = false) String subject,
                         @RequestParam String message,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        User user = currentUserService.get(authentication);
        String back = "REPORT".equalsIgnoreCase(type) ? "report" : "contact";
        try {
            supportService.submit(user, type, subject, message);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/" + back;
        }
        return "redirect:/" + back + "?sent=true";
    }

    @GetMapping("/admin/reports")
    public String adminReports(Authentication authentication, Model model) {
        model.addAttribute("user", currentUserService.get(authentication));
        model.addAttribute("messages", supportService.listAll());
        return "admin-reports";
    }

    @PostMapping("/admin/reports/{id}/resolve")
    public String resolve(@PathVariable Long id) {
        try {
            supportService.resolve(id);
        } catch (NoSuchElementException e) {
            return "redirect:/admin/reports?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
        return "redirect:/admin/reports?resolved=true";
    }
}
