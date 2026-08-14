package com.healthcare.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Sprint 3 - Medicine History log.
 * Records each time a scheduled dose was marked taken/missed/skipped.
 */
@Entity
@Table(name = "medicine_logs")
public class MedicineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_id", nullable = false)
    private MedicineReminder reminder;

    @Column(name = "scheduled_time", length = 10)
    private String scheduledTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicineLogStatus status;

    private LocalDateTime loggedAt = LocalDateTime.now();

    public MedicineLog() {}

    public MedicineLog(MedicineReminder reminder, String scheduledTime, MedicineLogStatus status) {
        this.reminder = reminder;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }

    public Long getId() { return id; }
    public MedicineReminder getReminder() { return reminder; }
    public void setReminder(MedicineReminder reminder) { this.reminder = reminder; }
    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
    public MedicineLogStatus getStatus() { return status; }
    public void setStatus(MedicineLogStatus status) { this.status = status; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}
