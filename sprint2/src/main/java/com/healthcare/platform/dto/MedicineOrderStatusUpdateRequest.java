package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotBlank;

/** status: CONFIRMED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED */
public record MedicineOrderStatusUpdateRequest(
        @NotBlank String status
) {
}
