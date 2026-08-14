package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotBlank;

/** status: IN_PROGRESS, COMPLETED, CANCELLED */
public record ConsultationStatusUpdateRequest(
        @NotBlank String status
) {
}
