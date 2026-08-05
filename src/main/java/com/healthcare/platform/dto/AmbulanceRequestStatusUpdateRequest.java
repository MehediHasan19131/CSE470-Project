package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotBlank;

/** status: ACCEPTED, EN_ROUTE, COMPLETED, CANCELLED */
public record AmbulanceRequestStatusUpdateRequest(
        @NotBlank String status
) {
}
