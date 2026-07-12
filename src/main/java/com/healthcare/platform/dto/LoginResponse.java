package com.healthcare.platform.dto;

import com.healthcare.platform.model.UserRole;

public record LoginResponse(
        String message,
        String sessionType,
        UserRole role,
        String fullName
) {
}
