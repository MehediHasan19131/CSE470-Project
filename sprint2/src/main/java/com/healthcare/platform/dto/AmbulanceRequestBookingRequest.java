package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AmbulanceRequestBookingRequest(
        @NotBlank String pickupAddress,
        @NotNull Double pickupLatitude,
        @NotNull Double pickupLongitude,
        String dropAddress,
        Double dropLatitude,
        Double dropLongitude,
        String emergencyType,
        String notes
) {
}
