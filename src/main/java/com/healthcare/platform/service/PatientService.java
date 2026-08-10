package com.healthcare.platform.service;

import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.dto.PatientRequest;
import com.healthcare.platform.dto.PatientResponse;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Doctor & Patient Module (Sprint 1) - Imtiaz Zaman Sami (23101551)
 * <p>
 * Patients follow the same User + Profile pattern as doctors: a {@link User} row
 * (role = PATIENT) with a linked {@link Profile} row carrying patient-specific
 * fields (date of birth, gender, blood group, address, etc).
 */
@Service
public class PatientService {
    private final UserRepository users;
    private final ProfileRepository profiles;
    private final PasswordEncoder passwordEncoder;

    public PatientService(UserRepository users, ProfileRepository profiles, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.profiles = profiles;
        this.passwordEncoder = passwordEncoder;
    }

    public List<PatientResponse> getAllPatients() {
        return users.findByRoleAndActiveTrue(UserRole.PATIENT).stream()
                .map(this::toResponse)
                .toList();
    }

    public PatientResponse getPatientById(Long id) {
        return toResponse(getPatientUser(id));
    }

    public PatientResponse createPatient(PatientRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required to create a patient account");
        }
        if (users.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists");
        }

        User user = new User(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                UserRole.PATIENT,
                request.phone(),
                true
        );
        user = users.save(user);

        Profile profile = new Profile(user, request.address(), request.city(), request.bio(), null, null, null, false, null, null);
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setGender(request.gender());
        profile.setBloodGroup(request.bloodGroup());
        profiles.save(profile);

        return toResponse(user);
    }

    public PatientResponse updatePatient(Long id, PatientRequest request) {
        User user = getPatientUser(id);

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        user = users.save(user);
        final User savedUser = user;

        Profile profile = profiles.findByUserId(user.getId())
                .orElseGet(() -> new Profile(savedUser, null, null, null, null, null, null, false, null, null));
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setGender(request.gender());
        profile.setBloodGroup(request.bloodGroup());
        profile.setCity(request.city());
        profile.setAddress(request.address());
        profile.setBio(request.bio());
        profiles.save(profile);

        return toResponse(user);
    }

    public void deletePatient(Long id) {
        User user = getPatientUser(id);
        profiles.findByUserId(user.getId()).ifPresent(profiles::delete);
        users.delete(user);
    }

    private User getPatientUser(Long id) {
        User user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id: " + id));
        if (user.getRole() != UserRole.PATIENT) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id: " + id);
        }
        return user;
    }

    private PatientResponse toResponse(User user) {
        Profile profile = profiles.findByUserId(user.getId()).orElse(null);
        return PatientResponse.from(user, profile);
    }
}
