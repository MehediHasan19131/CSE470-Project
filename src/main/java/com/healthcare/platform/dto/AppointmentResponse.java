package com.healthcare.platform.dto;

import com.healthcare.platform.model.Appointment;
import java.time.Duration;
import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long doctorId,
        String doctorName,
        String doctorSpecialization,
        Long patientId,
        String patientName,
        String patientPhone,
        LocalDateTime scheduledAt,
        String status,
        String reason,
        boolean reminderDue
) {
    public static AppointmentResponse from(Appointment appointment, String doctorSpecialization) {
        boolean reminderDue = "confirmed".equalsIgnoreCase(appointment.getStatus())
                && appointment.getScheduledAt() != null
                && !appointment.getScheduledAt().isBefore(LocalDateTime.now())
                && Duration.between(LocalDateTime.now(), appointment.getScheduledAt()).toHours() <= 24;
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getFullName(),
                doctorSpecialization,
                appointment.getPatient().getId(),
                appointment.getPatient().getFullName(),
                appointment.getPatient().getPhone(),
                appointment.getScheduledAt(),
                appointment.getStatus(),
                appointment.getReason(),
                reminderDue
        );
    }
}
