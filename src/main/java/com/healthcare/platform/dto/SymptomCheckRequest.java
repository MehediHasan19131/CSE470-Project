package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record SymptomCheckRequest(
        String sessionId,
        @NotBlank String symptoms
) {
}
