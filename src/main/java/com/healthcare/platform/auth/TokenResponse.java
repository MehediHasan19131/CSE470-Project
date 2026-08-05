package com.healthcare.platform.auth;

import com.healthcare.platform.model.UserRole;

public record TokenResponse(
        String token,
        String tokenType,
        UserRole role,
        String fullName,
        long expiresInMs
) {
}
