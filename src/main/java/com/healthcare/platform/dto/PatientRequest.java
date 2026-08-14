package com.healthcare.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * Doctor & Patient Module (Sprint 1) - Imtiaz Zaman Sami (23101551)
 * Request body used to create or update a patient (User + Profile).
 * password is only required on create; leave it blank on update to keep the existing password.
 */
public record PatientRequest(
        @NotBlank(message = "Full name is required") String fullName,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        String phone,
        String password,
        LocalDate dateOfBirth,
        String gender,
        String bloodGroup,
        String city,
        String address,
        String bio
) {
}
