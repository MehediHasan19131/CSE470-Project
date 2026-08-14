package com.healthcare.platform.dto;

import com.healthcare.platform.model.Ambulance;
import com.healthcare.platform.model.AmbulanceRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AmbulanceRequestResponse(
        Long id,
        String status,
        String pickupAddress,
        double pickupLatitude,
        double pickupLongitude,
        String dropAddress,
        Double dropLatitude,
        Double dropLongitude,
        String emergencyType,
        String notes,
        BigDecimal fareEstimate,
        LocalDateTime requestedAt,
        LocalDateTime updatedAt,
        Long patientId,
        String patientName,
        String patientPhone,
        Long ambulanceId,
        String vehicleNumber,
        String driverName,
        String driverPhone,
        Double ambulanceLatitude,
        Double ambulanceLongitude
) {
    public static AmbulanceRequestResponse from(AmbulanceRequest request) {
        Ambulance ambulance = request.getAmbulance();
        return new AmbulanceRequestResponse(
                request.getId(),
                request.getStatus(),
                request.getPickupAddress(),
                request.getPickupLatitude(),
                request.getPickupLongitude(),
                request.getDropAddress(),
                request.getDropLatitude(),
                request.getDropLongitude(),
                request.getEmergencyType(),
                request.getNotes(),
                request.getFareEstimate(),
                request.getRequestedAt(),
                request.getUpdatedAt(),
                request.getPatient().getId(),
                request.getPatient().getFullName(),
                request.getPatient().getPhone(),
                ambulance == null ? null : ambulance.getId(),
                ambulance == null ? null : ambulance.getVehicleNumber(),
                ambulance == null ? null : ambulance.getDriverName(),
                ambulance == null ? null : ambulance.getDriverPhone(),
                ambulance == null ? null : ambulance.getLatitude(),
                ambulance == null ? null : ambulance.getLongitude()
        );
    }
}
