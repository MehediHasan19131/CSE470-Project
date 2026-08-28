package com.healthcare.platform.dto.healthprofile;
import com.healthcare.platform.model.healthprofile.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MedicalHistoryResponse(
        Long id,
        Long patientId,
        String condition,
        LocalDate diagnosedOn,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MedicalHistoryResponse from(MedicalHistoryEntry entry) {
        return new MedicalHistoryResponse(
                entry.getId(),
                entry.getPatientId(),
                entry.getCondition(),
                entry.getDiagnosedOn(),
                entry.getNotes(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}
