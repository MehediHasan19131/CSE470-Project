package com.healthcare.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Doctor & Patient Module (Sprint 1) - Imtiaz Zaman Sami (23101551)
 * Request body used to create or update a doctor (User + Profile).
 * password is only required on create; leave it blank on update to keep the existing password.
 */
public record DoctorRequest(
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        String phone,
        String password,
        @NotBlank(message = "Specialization is required") String specialization,
        String licenseNumber,
        String qualification,
        Integer experienceYears,
        String city,
        String address,
        String bio,
        Double consultationFee
) {
}
