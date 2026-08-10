package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Notifications Module (Sprint 3) - Imtiaz Zaman Sami (23101551)
 * Frontend: Notification Center -> /notifications
 */
@Controller
public class NotificationWebController {
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationWebController(NotificationService notificationService, CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/notifications")
    public String notificationCenter(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("notifications", notificationService.getNotificationsForUser(user.getId()));
        return "notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead(Authentication authentication) {
        User user = currentUserService.get(authentication);
        notificationService.markAllAsRead(user);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        notificationService.markAsRead(id, user);
        return "redirect:/notifications";
    }
}
