package com.healthcare.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 * Request body used to make a donation (triggers the simulated payment).
 */
public record DonationRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.0", message = "Amount must be greater than 0") BigDecimal amount,
        String message,
        @NotBlank(message = "Payment method is required") String paymentMethod
) {
}
