package com.healthcare.platform.healthprofile;

import java.time.LocalDateTime;

public record AllergyResponse(
        Long id,
        Long patientId,
        String allergen,
        AllergySeverity severity,
        String reaction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AllergyResponse from(Allergy allergy) {
        return new AllergyResponse(
                allergy.getId(),
                allergy.getPatientId(),
                allergy.getAllergen(),
                allergy.getSeverity(),
                allergy.getReaction(),
                allergy.getCreatedAt(),
                allergy.getUpdatedAt()
        );
    }
}
