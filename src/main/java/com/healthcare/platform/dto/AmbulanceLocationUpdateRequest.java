package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotNull;

public record AmbulanceLocationUpdateRequest(
        @NotNull Double latitude,
        @NotNull Double longitude
) {
}
