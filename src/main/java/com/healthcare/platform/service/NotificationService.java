package com.healthcare.platform.service;

import com.healthcare.platform.model.Notification;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.NotificationRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Notifications Module (Sprint 3) - Imtiaz Zaman Sami (23101551)
 * Database: Notifications. Creates in-app notifications and triggers the
 * matching Email Notification for each one.
 */
@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notifications, EmailService emailService) {
        this.notifications = notifications;
        this.emailService = emailService;
    }

    public Notification createNotification(User user, String title, String message, String type, Long relatedAppointmentId) {
        Notification notification = new Notification(user, title, message, type, relatedAppointmentId);
        notification = notifications.save(notification);

        // Email Notifications
        emailService.sendEmail(user.getEmail(), title, message);

        return notification;
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        return notifications.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notifications.countByUserIdAndReadFalse(userId);
    }

    public void markAsRead(Long notificationId, User requester) {
        Notification notification = notifications.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found with id: " + notificationId));

        if (!notification.getUser().getId().equals(requester.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this notification");
        }
        notification.setRead(true);
        notifications.save(notification);
    }

    public void markAllAsRead(User user) {
        List<Notification> mine = notifications.findByUserIdOrderByCreatedAtDesc(user.getId());
        for (Notification notification : mine) {
            if (!notification.isRead()) {
                notification.setRead(true);
                notifications.save(notification);
            }
        }
    }
}
