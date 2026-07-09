package com.healthcare.platform.dto;

import com.healthcare.platform.model.UserRole;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(@NotNull UserRole role) {
}
