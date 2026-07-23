package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotBlank;

/** status: confirmed, completed, cancelled */
public record AppointmentStatusUpdateRequest(
        @NotBlank String status
) {
}
