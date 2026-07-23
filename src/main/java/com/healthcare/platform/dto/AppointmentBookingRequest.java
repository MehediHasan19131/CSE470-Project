package com.healthcare.platform.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentBookingRequest(
        @NotNull Long doctorId,
        @NotNull LocalDateTime scheduledAt,
        String reason
) {
}
