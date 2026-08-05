package com.healthcare.platform.dto;

import com.healthcare.platform.model.Consultation;
import java.time.LocalDateTime;

public record ConsultationResponse(
        Long id,
        Long appointmentId,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        String roomName,
        String jitsiDomain,
        String status,
        String notes,
        String prescription,
        LocalDateTime scheduledAt,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        LocalDateTime createdAt
) {
    public static ConsultationResponse from(Consultation consultation) {
        return new ConsultationResponse(
                consultation.getId(),
                consultation.getAppointment().getId(),
                consultation.getPatient().getId(),
                consultation.getPatient().getFullName(),
                consultation.getDoctor().getId(),
                consultation.getDoctor().getFullName(),
                consultation.getRoomName(),
                "meet.jit.si",
                consultation.getStatus(),
                consultation.getNotes(),
                consultation.getPrescription(),
                consultation.getScheduledAt(),
                consultation.getStartedAt(),
                consultation.getEndedAt(),
                consultation.getCreatedAt()
        );
    }
}
