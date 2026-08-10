package com.healthcare.platform.dto;

import com.healthcare.platform.model.Notification;
import java.time.LocalDateTime;

/**
 * Notifications Module (Sprint 3) - Imtiaz Zaman Sami (23101551)
 */
public record NotificationResponse(
        Long id,
        String title,
        String message,
        String type,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
