package com.healthcare.platform.dto;

import java.time.LocalDateTime;

public record ChatResponse(
        String sessionId,
        String reply,
        LocalDateTime respondedAt
) {
}
