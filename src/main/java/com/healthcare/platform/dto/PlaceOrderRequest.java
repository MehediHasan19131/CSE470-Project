package com.healthcare.platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Pharmacy Service Module (Sprint 2) - Imtiaz Zaman Sami (23101551)
 * Request body used to place an order for a single medicine.
 */
public record PlaceOrderRequest(
        @NotNull(message = "Medicine is required") Long medicineId,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity,
        String deliveryAddress
) {
}
