package com.healthcare.platform.healthprofile;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Plain Java object for one row of the `medical_history` table, used ONLY by
 * {@link HealthProfileJdbcRepository}. No JPA @Entity here either - same
 * "no ORM" rule Member 1 used for {@code AuthUser} and Member 2 used for
 * {@code Review}, applied to this sprint's tables.
 * <p>
 * One row = one condition/diagnosis/procedure a patient has recorded about
 * themselves (e.g. "Type 2 Diabetes", "Appendectomy") - a patient can have
 * any number of these, unlike a review which is capped at one per (reviewer,
 * target) pair.
 */
public class MedicalHistoryEntry {

    private Long id;
    private Long patientId;
    private String condition;
    private LocalDate diagnosedOn;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MedicalHistoryEntry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public LocalDate getDiagnosedOn() {
        return diagnosedOn;
    }

    public void setDiagnosedOn(LocalDate diagnosedOn) {
        this.diagnosedOn = diagnosedOn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
