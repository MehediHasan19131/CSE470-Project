package com.healthcare.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 * Request body used to create or update a campaign.
 */
public record CampaignRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Description is required") String description,
        String category,
        @NotNull(message = "Goal amount is required")
        @DecimalMin(value = "1.0", message = "Goal amount must be greater than 0") BigDecimal goalAmount
) {
}
