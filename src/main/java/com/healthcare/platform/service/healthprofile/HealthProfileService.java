package com.healthcare.platform.service.healthprofile;
import com.healthcare.platform.model.healthprofile.*;
import com.healthcare.platform.repository.healthprofile.*;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business rules for the Health Profile feature (Member 1, Sprint 3 task:
 * Database: Medical History, Allergies · Backend: Add History, Update History ·
 * Frontend: Health Profile Page).
 * <p>
 * A patient can only ever see or change their own records - every method here
 * takes the requester's id and checks it against the record's {@code patientId}
 * before allowing an update or delete, the same ownership check
 * {@code ReviewService.updateReview(...)} uses for "only the original reviewer
 * may update it".
 */
@Service
public class HealthProfileService {

    private final HealthProfileJdbcRepository healthProfile;

    public HealthProfileService(HealthProfileJdbcRepository healthProfile) {
        this.healthProfile = healthProfile;
    }

    // ---------------------------------------------------------------------
    // Medical history - "Add History" / "Update History"
    // ---------------------------------------------------------------------

    public List<MedicalHistoryEntry> getHistory(Long patientId) {
        return healthProfile.findHistoryByPatient(patientId);
    }

    /** "Add History" - creates a new medical history entry for the given patient. */
    public MedicalHistoryEntry addHistory(Long patientId, String condition, LocalDate diagnosedOn, String notes) {
        String normalizedCondition = requireCondition(condition);

        MedicalHistoryEntry entry = new MedicalHistoryEntry();
        entry.setPatientId(patientId);
        entry.setCondition(normalizedCondition);
        entry.setDiagnosedOn(diagnosedOn);
        entry.setNotes(normalizeNotes(notes));

        return healthProfile.insertHistory(entry);
    }

    /** "Update History" - edits an existing medical history entry. Only the owning patient may update it. */
    public MedicalHistoryEntry updateHistory(Long id, Long requesterId, String condition, LocalDate diagnosedOn, String notes) {
        String normalizedCondition = requireCondition(condition);

        MedicalHistoryEntry existing = healthProfile.findHistoryById(id)
                .orElseThrow(() -> new NoSuchElementException("Medical history entry not found."));

        if (!existing.getPatientId().equals(requesterId)) {
            throw new IllegalStateException("You can only update your own medical history.");
        }

        healthProfile.updateHistory(id, normalizedCondition, diagnosedOn, normalizeNotes(notes));
        return healthProfile.findHistoryById(id).orElseThrow();
    }

    public void deleteHistory(Long id, Long requesterId) {
        MedicalHistoryEntry existing = healthProfile.findHistoryById(id)
                .orElseThrow(() -> new NoSuchElementException("Medical history entry not found."));

        if (!existing.getPatientId().equals(requesterId)) {
            throw new IllegalStateException("You can only delete your own medical history.");
        }

        healthProfile.deleteHistory(id);
    }

    // ---------------------------------------------------------------------
    // Allergies
    // ---------------------------------------------------------------------

    public List<Allergy> getAllergies(Long patientId) {
        return healthProfile.findAllergiesByPatient(patientId);
    }

    public Allergy addAllergy(Long patientId, String allergen, AllergySeverity severity, String reaction) {
        String normalizedAllergen = requireAllergen(allergen);

        Allergy allergy = new Allergy();
        allergy.setPatientId(patientId);
        allergy.setAllergen(normalizedAllergen);
        allergy.setSeverity(severity != null ? severity : AllergySeverity.MODERATE);
        allergy.setReaction(normalizeNotes(reaction));

        return healthProfile.insertAllergy(allergy);
    }

    public Allergy updateAllergy(Long id, Long requesterId, String allergen, AllergySeverity severity, String reaction) {
        String normalizedAllergen = requireAllergen(allergen);

        Allergy existing = healthProfile.findAllergyById(id)
                .orElseThrow(() -> new NoSuchElementException("Allergy not found."));

        if (!existing.getPatientId().equals(requesterId)) {
            throw new IllegalStateException("You can only update your own allergies.");
        }

        healthProfile.updateAllergy(id, normalizedAllergen, severity != null ? severity : AllergySeverity.MODERATE, normalizeNotes(reaction));
        return healthProfile.findAllergyById(id).orElseThrow();
    }

    public void deleteAllergy(Long id, Long requesterId) {
        Allergy existing = healthProfile.findAllergyById(id)
                .orElseThrow(() -> new NoSuchElementException("Allergy not found."));

        if (!existing.getPatientId().equals(requesterId)) {
            throw new IllegalStateException("You can only delete your own allergies.");
        }

        healthProfile.deleteAllergy(id);
    }

    /** Called from {@code AdminUserService.deleteUser(...)} before deleting the user row - see that class for why. */
    public void deleteAllForPatient(Long patientId) {
        healthProfile.deleteHistoryForPatient(patientId);
        healthProfile.deleteAllergiesForPatient(patientId);
    }

    private String requireCondition(String condition) {
        if (condition == null || condition.isBlank()) {
            throw new IllegalArgumentException("Condition is required.");
        }
        return condition.trim();
    }

    private String requireAllergen(String allergen) {
        if (allergen == null || allergen.isBlank()) {
            throw new IllegalArgumentException("Allergen is required.");
        }
        return allergen.trim();
    }

    private String normalizeNotes(String notes) {
        return (notes == null || notes.isBlank()) ? null : notes.trim();
    }
}
