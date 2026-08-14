package com.healthcare.platform.healthprofile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** What we receive from {@code POST /api/health/allergies}. */
public record AllergyCreateRequest(
        @NotBlank @Size(max = 150) String allergen,
        @NotNull AllergySeverity severity,
        @Size(max = 500, message = "Reaction notes can't be longer than 500 characters") String reaction
) {
}
