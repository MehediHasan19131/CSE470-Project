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
import java.time.LocalDateTime;

/**
 * Hospital & Diagnostic Module (Member 3) - Bed availability.
 * One row per ward/bed type a hospital tracks (e.g. "General", "ICU", "Cabin").
 */
@Entity
@Table(name = "bed_availabilities")
public class BedAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private User hospital;

    @Column(name = "ward_type", nullable = false, length = 60)
    private String wardType;

    @Column(name = "total_beds", nullable = false)
    private int totalBeds;

    @Column(name = "available_beds", nullable = false)
    private int availableBeds;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public BedAvailability() {
    }

    public BedAvailability(User hospital, String wardType, int totalBeds, int availableBeds) {
        this.hospital = hospital;
        this.wardType = wardType;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
    }

    public Long getId() {
        return id;
    }

    public User getHospital() {
        return hospital;
    }

    public String getWardType() {
        return wardType;
    }

    public void setWardType(String wardType) {
        this.wardType = wardType;
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public void setTotalBeds(int totalBeds) {
        this.totalBeds = totalBeds;
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
