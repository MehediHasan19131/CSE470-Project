package com.healthcare.platform.dto;

import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.User;

/**
 * Doctor & Patient Module (Sprint 1) - Imtiaz Zaman Sami (23101551)
 */
public record DoctorResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        boolean active,
        String specialization,
        String licenseNumber,
        String qualification,
        Integer experienceYears,
        Double consultationFee,
        String city,
        String address,
        String bio,
        double averageRating,
        long totalReviews
) {
    public static DoctorResponse from(User user, Profile profile, double averageRating, long totalReviews) {
        return new DoctorResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive(),
                profile == null ? null : profile.getSpecialization(),
                profile == null ? null : profile.getLicenseNumber(),
                profile == null ? null : profile.getQualification(),
                profile == null ? null : profile.getExperienceYears(),
                profile == null ? null : profile.getConsultationFee(),
                profile == null ? null : profile.getCity(),
                profile == null ? null : profile.getAddress(),
                profile == null ? null : profile.getBio(),
                Math.round(averageRating * 10.0) / 10.0,
                totalReviews
        );
    }
}
