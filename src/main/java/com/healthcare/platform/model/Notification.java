package com.healthcare.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Notifications Module (Sprint 3) - Imtiaz Zaman Sami (23101551)
 * A single notification delivered to a user (in-app + optionally email).
 */
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // e.g. "APPOINTMENT_REMINDER", "ORDER", "GENERAL"
    @Column(nullable = false, length = 40)
    private String type;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    // Optional link back to the appointment this notification is about,
    // used to avoid sending duplicate Appointment Reminders.
    @Column(name = "related_appointment_id")
    private Long relatedAppointmentId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {
    }

    public Notification(User user, String title, String message, String type, Long relatedAppointmentId) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedAppointmentId = relatedAppointmentId;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Long getRelatedAppointmentId() {
        return relatedAppointmentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

