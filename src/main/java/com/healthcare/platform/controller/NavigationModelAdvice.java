package com.healthcare.platform.controller;

import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.NotificationService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Adds the unread count to every authenticated page so the notification bell is useful for every role. */
@ControllerAdvice
public class NavigationModelAdvice {
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    public NavigationModelAdvice(CurrentUserService currentUserService, NotificationService notificationService) {
        this.currentUserService = currentUserService;
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addNavigationData(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) return;
        try {
            model.addAttribute("unreadNotificationCount", notificationService.getUnreadCount(currentUserService.get(authentication).getId()));
        } catch (RuntimeException ignored) {
            // Navigation must never make an otherwise valid page unavailable.
        }
    }
}
