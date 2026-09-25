package com.healthcare.platform.dto.healthprofile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** What we receive from {@code PUT /api/health/history/{id}} - "Update History". */
public record MedicalHistoryUpdateRequest(
        @NotBlank @Size(max = 150) String condition,
        LocalDate diagnosedOn,
        @Size(max = 1000, message = "Notes can't be longer than 1000 characters") String notes
) {
}
