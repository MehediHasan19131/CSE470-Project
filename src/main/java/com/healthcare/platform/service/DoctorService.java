package com.healthcare.platform.service;

import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.dto.DoctorRequest;
import com.healthcare.platform.dto.DoctorResponse;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Doctor & Patient Module (Sprint 1) - Imtiaz Zaman Sami (23101551)
 * <p>
 * Doctors are stored the same way every other role is stored in this codebase:
 * a {@link User} row (role = DOCTOR) with a linked {@link Profile} row that carries
 * the doctor-specific fields (specialization, license number, qualification, fee, etc).
 */
@Service
public class DoctorService {
    private final UserRepository users;
    private final ProfileRepository profiles;
    private final RatingRepository ratings;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(UserRepository users, ProfileRepository profiles, RatingRepository ratings, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.profiles = profiles;
        this.ratings = ratings;
        this.passwordEncoder = passwordEncoder;
    }

    public List<DoctorResponse> getAllDoctors() {
        return users.findByRoleAndActiveTrue(UserRole.DOCTOR).stream()
                .map(this::toResponse)
                .toList();
    }

    public DoctorResponse getDoctorById(Long id) {
        return toResponse(getDoctorUser(id));
    }

    public DoctorResponse createDoctor(DoctorRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required to create a doctor account");
        }
        if (users.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists");
        }

        User user = new User(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                UserRole.DOCTOR,
                request.phone(),
                true
        );
        user = users.save(user);

        Profile profile = new Profile(
                user,
                request.address(),
                request.city(),
                request.bio(),
                request.specialization(),
                request.licenseNumber(),
                null,
                false,
                null,
                null
        );
        profile.setQualification(request.qualification());
        profile.setExperienceYears(request.experienceYears());
        profile.setConsultationFee(request.consultationFee());
        profiles.save(profile);

        return toResponse(user);
    }

    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        User user = getDoctorUser(id);

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        user = users.save(user);
        final User savedUser = user;

        Profile profile = profiles.findByUserId(user.getId()).orElseGet(() -> {
            Profile created = new Profile(savedUser, null, null, null, null, null, null, false, null, null);
            return created;
        });
        profile.setSpecialization(request.specialization());
        profile.setLicenseNumber(request.licenseNumber());
        profile.setQualification(request.qualification());
        profile.setExperienceYears(request.experienceYears());
        profile.setConsultationFee(request.consultationFee());
        profile.setCity(request.city());
        profile.setAddress(request.address());
        profile.setBio(request.bio());
        profiles.save(profile);

        return toResponse(user);
    }

    public void deleteDoctor(Long id) {
        User user = getDoctorUser(id);
        profiles.findByUserId(user.getId()).ifPresent(profiles::delete);
        users.delete(user);
    }

    // Search Doctor by Specialty
    public List<DoctorResponse> searchBySpecialty(String specialty) {
        if (specialty == null || specialty.isBlank()) {
            return getAllDoctors();
        }
        String needle = specialty.trim().toLowerCase();
        return users.findByRoleAndActiveTrue(UserRole.DOCTOR).stream()
                .filter(user -> {
                    Profile profile = profiles.findByUserId(user.getId()).orElse(null);
                    return profile != null
                            && profile.getSpecialization() != null
                            && profile.getSpecialization().toLowerCase().contains(needle);
                })
                .map(this::toResponse)
                .toList();
    }

    private User getDoctorUser(Long id) {
        User user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id: " + id));
        if (user.getRole() != UserRole.DOCTOR) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id: " + id);
        }
        return user;
    }

    private DoctorResponse toResponse(User user) {
        Profile profile = profiles.findByUserId(user.getId()).orElse(null);
        double avg = ratings.averageScore(user.getId());
        long total = ratings.countByTargetUserId(user.getId());
        return DoctorResponse.from(user, profile, avg, total);
    }
}
