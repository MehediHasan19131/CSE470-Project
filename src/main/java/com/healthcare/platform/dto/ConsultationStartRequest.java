package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotNull;

public record ConsultationStartRequest(
        @NotNull Long appointmentId
) {
}
