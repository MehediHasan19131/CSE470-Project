package com.healthcare.platform.dto;

import java.time.LocalDate;

import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.User;

/**
 * Doctor & Patient Module (Sprint 1) - Imtiaz Zaman Sami (23101551)
 */
public record PatientResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        boolean active,
        LocalDate dateOfBirth,
        String gender,
        String bloodGroup,
        String city,
        String address,
        String bio
) {
    public static PatientResponse from(User user, Profile profile) {
        return new PatientResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive(),
                profile == null ? null : profile.getDateOfBirth(),
                profile == null ? null : profile.getGender(),
                profile == null ? null : profile.getBloodGroup(),
                profile == null ? null : profile.getCity(),
                profile == null ? null : profile.getAddress(),
                profile == null ? null : profile.getBio()
        );
    }
}
