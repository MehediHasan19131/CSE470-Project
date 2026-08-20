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
 * A "Contact us" message or a "Report a problem" submitted by any logged-in user
 * from the overflow (three-dots) menu. Admins review these from Admin &gt; Review
 * reports and mark them resolved.
 */
@Entity
@Table(name = "support_messages")
public class SupportMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // "CONTACT" or "REPORT"
    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 160)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // "OPEN" or "RESOLVED"
    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public SupportMessage() {
    }

    public SupportMessage(User sender, String type, String subject, String message) {
        this.sender = sender;
        this.type = type;
        this.subject = subject;
        this.message = message;
    }

    public Long getId() { return id; }
    public User getSender() { return sender; }
    public String getType() { return type; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
