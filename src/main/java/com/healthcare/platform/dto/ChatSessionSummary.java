package com.healthcare.platform.dto;

import java.time.LocalDateTime;

public record ChatSessionSummary(
        String sessionId,
        String preview,
        String mode,
        LocalDateTime lastMessageAt
) {
}
