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
import java.time.LocalTime;

/**
 * Hospital & Diagnostic Module (Member 3) - Doctor availability.
 * Which existing DOCTOR-role users see patients at this hospital, and when.
 * Separate from a doctor's own appointment schedule (Appointment module) -
 * this is the hospital advertising who's on-site, not booking a specific slot.
 */
@Entity
@Table(name = "hospital_doctor_availabilities")
public class HospitalDoctorAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private User hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    // MON, TUE, WED, THU, FRI, SAT, SUN
    @Column(name = "day_of_week", nullable = false, length = 3)
    private String dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(length = 255)
    private String notes;

    public HospitalDoctorAvailability() {
    }

    public HospitalDoctorAvailability(User hospital, User doctor, String dayOfWeek,
                                       LocalTime startTime, LocalTime endTime, String notes) {
        this.hospital = hospital;
        this.doctor = doctor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public User getHospital() {
        return hospital;
    }

    public User getDoctor() {
        return doctor;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
