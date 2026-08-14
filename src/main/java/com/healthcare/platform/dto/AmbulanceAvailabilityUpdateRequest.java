package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotNull;

public record AmbulanceAvailabilityUpdateRequest(
        @NotNull Boolean available
) {
}
