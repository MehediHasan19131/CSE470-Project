package com.healthcare.platform.controller;

import com.healthcare.platform.dto.NotificationResponse;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.AppointmentReminderScheduler;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.MedicineReminderScheduler;
import com.healthcare.platform.service.NotificationService;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Notifications Module (Sprint 3) - Imtiaz Zaman Sami (23101551)
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationApiController {
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;
    private final AppointmentReminderScheduler reminderScheduler;
    private final MedicineReminderScheduler medicineReminderScheduler;

    public NotificationApiController(
            NotificationService notificationService,
            CurrentUserService currentUserService,
            AppointmentReminderScheduler reminderScheduler,
            MedicineReminderScheduler medicineReminderScheduler
    ) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
        this.reminderScheduler = reminderScheduler;
        this.medicineReminderScheduler = medicineReminderScheduler;
    }

    @GetMapping
    public List<NotificationResponse> getMyNotifications(Authentication authentication) {
        User user = currentUserService.get(authentication);
        return notificationService.getNotificationsForUser(user.getId()).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(Authentication authentication) {
        User user = currentUserService.get(authentication);
        return Map.of("unreadCount", notificationService.getUnreadCount(user.getId()));
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        notificationService.markAsRead(id, user);
    }

    @PostMapping("/read-all")
    public void markAllAsRead(Authentication authentication) {
        User user = currentUserService.get(authentication);
        notificationService.markAllAsRead(user);
    }

    // Manually triggers the Appointment Reminder job (useful for demos -
    // no need to wait for the 5-minute schedule). Admin only.
    @PostMapping("/run-reminder-check")
    public Map<String, Integer> runReminderCheck() {
        int sent = reminderScheduler.runReminderCheck();
        return Map.of("remindersSent", sent);
    }

    // Same idea, for Medicine Reminders. Admin only.
    @PostMapping("/run-medicine-reminder-check")
    public Map<String, Integer> runMedicineReminderCheck() {
        int sent = medicineReminderScheduler.runReminderCheck();
        return Map.of("remindersSent", sent);
    }
}
