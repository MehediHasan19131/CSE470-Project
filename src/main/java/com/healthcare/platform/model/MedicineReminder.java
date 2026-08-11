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
import java.time.LocalTime;

/**
 * Medicine Reminder: a patient sets a daily reminder to take a medicine at a
 * given time. {@link com.healthcare.platform.service.MedicineReminderScheduler}
 * checks these against the clock and, once per day per reminder, creates a
 * Notification (Sprint 3 - Notifications Module) the same way
 * {@link com.healthcare.platform.service.AppointmentReminderScheduler} does
 * for upcoming appointments - {@code lastTriggeredDate} is what stops it
 * firing more than once on the same day.
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

    @Column(name = "medicine_name", nullable = false, length = 150)
    private String medicineName;

    @Column(length = 80)
    private String dosage;

    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime;

    @Column(length = 300)
    private String notes;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_triggered_date")
    private LocalDate lastTriggeredDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MedicineReminder() {}

    public MedicineReminder(User patient, String medicineName, String dosage, LocalTime reminderTime, String notes) {
        this.patient = patient;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.reminderTime = reminderTime;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public User getPatient() { return patient; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public LocalTime getReminderTime() { return reminderTime; }
    public void setReminderTime(LocalTime reminderTime) { this.reminderTime = reminderTime; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDate getLastTriggeredDate() { return lastTriggeredDate; }
    public void setLastTriggeredDate(LocalDate lastTriggeredDate) { this.lastTriggeredDate = lastTriggeredDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
