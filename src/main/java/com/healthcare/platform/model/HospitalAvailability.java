package com.healthcare.platform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hospital_availability")
public class HospitalAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_user_id", nullable = false, unique = true)
    private User hospital;

    private int totalBeds;
    private int availableBeds;
    private int totalIcu;
    private int availableIcu;
    private int totalCcu;
    private int availableCcu;

    public HospitalAvailability() {
    }

    public HospitalAvailability(User hospital, int totalBeds, int availableBeds, int totalIcu, int availableIcu, int totalCcu, int availableCcu) {
        this.hospital = hospital;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
        this.totalIcu = totalIcu;
        this.availableIcu = availableIcu;
        this.totalCcu = totalCcu;
        this.availableCcu = availableCcu;
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public int getTotalIcu() {
        return totalIcu;
    }

    public int getAvailableIcu() {
        return availableIcu;
    }

    public int getTotalCcu() {
        return totalCcu;
    }

    public int getAvailableCcu() {
        return availableCcu;
    }
}
