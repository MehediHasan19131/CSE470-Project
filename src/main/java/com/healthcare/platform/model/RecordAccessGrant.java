package com.healthcare.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * Medical Records Sharing: a patient explicitly grants (and can later revoke)
 * a specific doctor permission to view their medical history and allergies
 * (see {@code com.healthcare.platform.healthprofile}). One row per
 * patient/doctor pair, re-used across grant/revoke cycles rather than
 * deleted, so there's a record of who has ever had access - "active" is what
 * actually controls whether the doctor can currently see anything.
 */
@Entity
@Table(name = "record_access_grants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"patient_id", "doctor_id"}))
public class RecordAccessGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public RecordAccessGrant() {}

    public RecordAccessGrant(User patient, User doctor) {
        this.patient = patient;
        this.doctor = doctor;
        this.active = true;
    }

    public Long getId() { return id; }
    public User getPatient() { return patient; }
    public User getDoctor() { return doctor; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
