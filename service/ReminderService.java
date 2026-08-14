package com.healthcare.platform.service.sprint3;

import com.healthcare.platform.model.MedicineLog;
import com.healthcare.platform.model.MedicineLogStatus;
import com.healthcare.platform.model.MedicineReminder;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.MedicineLogRepository;
import com.healthcare.platform.repository.MedicineReminderRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReminderService {

    private final MedicineReminderRepository reminders;
    private final MedicineLogRepository logs;

    public ReminderService(MedicineReminderRepository reminders, MedicineLogRepository logs) {
        this.reminders = reminders;
        this.logs = logs;
    }

    public List<MedicineReminder> activeFor(User patient) {
        return reminders.findActiveByPatientId(patient.getId());
    }

    public List<MedicineReminder> allFor(User patient) {
        return reminders.findAllByPatientId(patient.getId());
    }

    public MedicineReminder get(Long id, User currentUser) {
        return owned(id, currentUser);
    }

    @Transactional
    public MedicineReminder create(User patient, String medicineName, String dosage, int frequencyPerDay,
                                   String reminderTimes, LocalDate startDate, LocalDate endDate, String notes) {
        validate(medicineName, dosage, frequencyPerDay, reminderTimes, startDate, endDate);
        MedicineReminder reminder = new MedicineReminder(
                patient, medicineName, dosage, frequencyPerDay, reminderTimes,
                startDate == null ? LocalDate.now() : startDate, endDate, notes);
        reminder.setActive(true);
        reminder.setCreatedAt(LocalDateTime.now());
        reminder.setUpdatedAt(LocalDateTime.now());
        return reminders.save(reminder);
    }

    @Transactional
    public MedicineReminder update(Long id, User currentUser, String medicineName, String dosage,
                                   int frequencyPerDay, String reminderTimes,
                                   LocalDate startDate, LocalDate endDate, String notes) {
        MedicineReminder reminder = owned(id, currentUser);
        validate(medicineName, dosage, frequencyPerDay, reminderTimes, startDate, endDate);
        reminder.setMedicineName(medicineName);
        reminder.setDosage(dosage);
        reminder.setFrequencyPerDay(frequencyPerDay);
        reminder.setReminderTimes(reminderTimes);
        reminder.setStartDate(startDate);
        reminder.setEndDate(endDate);
        reminder.setNotes(notes);
        reminder.setUpdatedAt(LocalDateTime.now());
        return reminders.save(reminder);
    }

    @Transactional
    public void deactivate(Long id, User currentUser) {
        MedicineReminder reminder = owned(id, currentUser);
        reminder.setActive(false);
        reminder.setUpdatedAt(LocalDateTime.now());
        reminders.save(reminder);
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        MedicineReminder reminder = owned(id, currentUser);
        logs.deleteByReminderId(id);
        reminders.delete(reminder);
    }

    @Transactional
    public MedicineLog logDose(Long reminderId, User currentUser, String status, String scheduledTime) {
        MedicineReminder reminder = owned(reminderId, currentUser);
        MedicineLogStatus logStatus;
        try {
            logStatus = MedicineLogStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status. Use TAKEN, MISSED or SKIPPED.");
        }
        MedicineLog log = new MedicineLog(reminder, scheduledTime, logStatus);
        log.setLoggedAt(LocalDateTime.now());
        return logs.save(log);
    }

    public List<MedicineLog> history(User patient) {
        return logs.findHistoryByPatientId(patient.getId());
    }

    private void validate(String medicineName, String dosage, int frequencyPerDay,
                          String reminderTimes, LocalDate startDate, LocalDate endDate) {
        if (medicineName == null || medicineName.isBlank()) {
            throw new IllegalArgumentException("Medicine name is required.");
        }
        if (dosage == null || dosage.isBlank()) {
            throw new IllegalArgumentException("Dosage is required.");
        }
        if (frequencyPerDay < 1 || frequencyPerDay > 10) {
            throw new IllegalArgumentException("Frequency per day must be between 1 and 10.");
        }
        if (reminderTimes == null || reminderTimes.isBlank()) {
            throw new IllegalArgumentException("At least one reminder time is required.");
        }
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
    }

    private MedicineReminder owned(Long id, User currentUser) {
        MedicineReminder reminder = reminders.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        if (!reminder.getPatient().getId().equals(currentUser.getId())
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Not authorized to access this reminder");
        }
        return reminder;
    }
}
