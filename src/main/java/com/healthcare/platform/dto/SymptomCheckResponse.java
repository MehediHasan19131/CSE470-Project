package com.healthcare.platform.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SymptomCheckResponse(
        String sessionId,
        String assessment,
        String recommendedSpecialty,
        List<DoctorRecommendation> recommendedDoctors,
        LocalDateTime respondedAt
) {
}
