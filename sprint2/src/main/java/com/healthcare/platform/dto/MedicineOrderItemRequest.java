package com.healthcare.platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MedicineOrderItemRequest(
        @NotNull Long medicineId,
        @Min(1) int quantity
) {
}
