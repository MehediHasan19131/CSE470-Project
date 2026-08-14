package com.healthcare.platform.healthprofile;

import java.time.LocalDateTime;

/**
 * Plain Java object for one row of the `allergies` table, used ONLY by
 * {@link HealthProfileJdbcRepository}. No JPA @Entity - same "no ORM" rule
 * as the rest of the project.
 */
public class Allergy {

    private Long id;
    private Long patientId;
    private String allergen;
    private AllergySeverity severity;
    private String reaction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Allergy() {
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

    public String getAllergen() {
        return allergen;
    }

    public void setAllergen(String allergen) {
        this.allergen = allergen;
    }

    public AllergySeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AllergySeverity severity) {
        this.severity = severity;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
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
