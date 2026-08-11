package com.healthcare.platform.service;

import com.healthcare.platform.model.RecordAccessGrant;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.RecordAccessGrantRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Medical Records Sharing: lets a patient grant or revoke a specific doctor's
 * access to their medical history & allergies (com.healthcare.platform.healthprofile).
 * The grant itself only records *permission* - DoctorPatientRecordsController is
 * what actually enforces it before showing a doctor anything, re-checking
 * {@link #hasActiveAccess} on every request rather than trusting that the
 * "My Patients" list was filtered correctly, so a revoked doctor is locked out
 * immediately even with a bookmarked URL.
 */
@Service
public class RecordAccessService {

    private final RecordAccessGrantRepository grants;
    private final UserRepository users;

    public RecordAccessService(RecordAccessGrantRepository grants, UserRepository users) {
        this.grants = grants;
        this.users = users;
    }

    public List<User> listDoctors() {
        return users.findByRoleAndActiveTrue(UserRole.DOCTOR);
    }

    public List<RecordAccessGrant> listGrantsForPatient(Long patientId) {
        return grants.findByPatientIdOrderByUpdatedAtDesc(patientId);
    }

    public List<RecordAccessGrant> listActivePatientsForDoctor(Long doctorId) {
        return grants.findByDoctorIdAndActiveTrueOrderByUpdatedAtDesc(doctorId);
    }

    public boolean hasActiveAccess(Long patientId, Long doctorId) {
        return grants.existsByPatientIdAndDoctorIdAndActiveTrue(patientId, doctorId);
    }

    @Transactional
    public RecordAccessGrant grant(Long patientId, Long doctorId) {
        if (patientId.equals(doctorId)) {
            throw new IllegalArgumentException("Invalid doctor selected.");
        }
        User doctor = users.findById(doctorId)
                .filter(u -> u.getRole() == UserRole.DOCTOR)
                .orElseThrow(() -> new IllegalArgumentException("Selected doctor was not found."));
        User patient = users.findById(patientId).orElseThrow(() -> new NoSuchElementException("Patient not found."));

        RecordAccessGrant grant = grants.findByPatientIdAndDoctorId(patientId, doctorId)
                .orElseGet(() -> new RecordAccessGrant(patient, doctor));
        grant.setActive(true);
        return grants.save(grant);
    }

    @Transactional
    public void revoke(Long patientId, Long doctorId) {
        RecordAccessGrant grant = grants.findByPatientIdAndDoctorId(patientId, doctorId)
                .orElseThrow(() -> new NoSuchElementException("Access grant not found."));
        grant.setActive(false);
        grants.save(grant);
    }

    /** Called from AdminUserService.deleteUser(...) before deleting the user row - see that class for why. */
    @Transactional
    public void deleteAllForUser(Long userId) {
        grants.deleteByPatientId(userId);
        grants.deleteByDoctorId(userId);
    }
}
