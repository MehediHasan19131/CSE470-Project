package com.healthcare.platform.dto;

import com.healthcare.platform.model.AiChatMessage;
import java.time.LocalDateTime;

public record ChatMessageResponse(
        String role,
        String content,
        String mode,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(AiChatMessage message) {
        return new ChatMessageResponse(message.getRole(), message.getContent(), message.getMode(), message.getCreatedAt());
    }
}
