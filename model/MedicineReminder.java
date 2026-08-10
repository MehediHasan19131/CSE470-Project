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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Sprint 3 - Medicine Reminder.
 *
 * Reminder times are stored as a comma-separated list in a single column
 * (e.g. "08:00,14:00,20:00"). This keeps the sprint simple: the frontend
 * uses a single text input with helper text, and parsing is trivial. A
 * child MedicineReminderTime entity would be cleaner for production-grade
 * scheduling, but adds unneeded complexity for this module.
 */
@Entity
@Table(name = "medicine_reminders")
public class MedicineReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @Column(nullable = false, length = 120)
    private String medicineName;

    @Column(nullable = false, length = 80)
    private String dosage;

    @Column(name = "frequency_per_day", nullable = false)
    private int frequencyPerDay;

    @Column(name = "reminder_times", nullable = false, length = 200)
    private String reminderTimes;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public MedicineReminder() {}

    public MedicineReminder(User patient, String medicineName, String dosage, int frequencyPerDay,
                            String reminderTimes, LocalDate startDate, LocalDate endDate, String notes) {
        this.patient = patient;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequencyPerDay = frequencyPerDay;
        this.reminderTimes = reminderTimes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
    }

    public List<String> timeList() {
        if (reminderTimes == null || reminderTimes.isBlank()) return List.of();
        return Arrays.stream(reminderTimes.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public Long getId() { return id; }
    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public int getFrequencyPerDay() { return frequencyPerDay; }
    public void setFrequencyPerDay(int frequencyPerDay) { this.frequencyPerDay = frequencyPerDay; }
    public String getReminderTimes() { return reminderTimes; }
    public void setReminderTimes(String reminderTimes) { this.reminderTimes = reminderTimes; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
