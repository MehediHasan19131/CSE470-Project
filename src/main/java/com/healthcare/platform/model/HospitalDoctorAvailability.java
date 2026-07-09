package com.healthcare.platform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hospital_doctor_availability")
public class HospitalDoctorAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_user_id", nullable = false)
    private User hospital;

    private String doctorName;
    private String specialization;
    private boolean available;

    public HospitalDoctorAvailability() {
    }

    public HospitalDoctorAvailability(User hospital, String doctorName, String specialization, boolean available) {
        this.hospital = hospital;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.available = available;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public boolean isAvailable() {
        return available;
    }
}
