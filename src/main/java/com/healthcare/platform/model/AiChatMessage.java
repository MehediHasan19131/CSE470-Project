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
 * Sprint 4 — AI Chat History.
 * One row per message (user or assistant) in a conversation. Messages are grouped into a
 * conversation via sessionId (a client-generated UUID), the same way ChatGPT-style history works.
 */
@Entity
@Table(name = "ai_chat_messages")
public class AiChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;

    /** "user", "assistant", or "system" */
    @Column(nullable = false, length = 20)
    private String role;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** "chat" for general Q&A, "symptom_check" for the symptom-checker flow */
    @Column(length = 30)
    private String mode = "chat";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AiChatMessage() {
    }

    public AiChatMessage(User user, String sessionId, String role, String content, String mode) {
        this.user = user;
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.mode = mode;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getMode() {
        return mode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
