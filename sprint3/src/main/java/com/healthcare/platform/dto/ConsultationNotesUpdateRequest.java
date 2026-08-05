package com.healthcare.platform.dto;

public record ConsultationNotesUpdateRequest(
        String notes,
        String prescription
) {
}
