package com.healthcare.platform.dto;

import com.healthcare.platform.model.Ambulance;
import java.math.BigDecimal;

public record AmbulanceResponse(
        Long id,
        String vehicleNumber,
        String vehicleType,
        int capacity,
        boolean available,
        Double latitude,
        Double longitude,
        Long providerId,
        String providerName,
        String providerPhone,
        BigDecimal baseFare,
        BigDecimal perKmRate,
        Double distanceKm
) {
    public static AmbulanceResponse from(Ambulance ambulance, Double distanceKm) {
        return new AmbulanceResponse(
                ambulance.getId(),
                ambulance.getVehicleNumber(),
                ambulance.getVehicleType(),
                ambulance.getCapacity(),
                ambulance.isAvailable(),
                ambulance.getLatitude(),
                ambulance.getLongitude(),
                ambulance.getProvider().getId(),
                ambulance.getProvider().getFullName(),
                ambulance.getProvider().getPhone(),
                ambulance.getBaseFare(),
                ambulance.getPerKmRate(),
                distanceKm
        );
    }
}
