package com.healthcare.platform.dto;

import com.healthcare.platform.model.auth.AuthUser;
import com.healthcare.platform.model.UserRole;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        UserRole role,
        String phone,
        boolean active
) {
    public static UserResponse from(AuthUser user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getPhone(), user.isActive());
    }
}
