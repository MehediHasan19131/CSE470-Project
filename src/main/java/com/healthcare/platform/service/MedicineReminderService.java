package com.healthcare.platform.service;

import com.healthcare.platform.model.MedicineReminder;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.MedicineReminderRepository;
import com.healthcare.platform.repository.UserRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

/**
 * Medicine Reminder: a patient's own daily reminders to take a medicine. Same
 * ownership-check pattern as HealthProfileService - a patient can only view,
 * toggle, or delete their own reminders.
 */
@Service
public class MedicineReminderService {

    private final MedicineReminderRepository reminders;
    private final UserRepository users;

    public MedicineReminderService(MedicineReminderRepository reminders, UserRepository users) {
        this.reminders = reminders;
        this.users = users;
    }

    public List<MedicineReminder> listForPatient(Long patientId) {
        return reminders.findByPatientIdOrderByReminderTime(patientId);
    }

    public MedicineReminder create(Long patientId, String medicineName, String dosage, LocalTime reminderTime, String notes) {
        if (medicineName == null || medicineName.isBlank()) {
            throw new IllegalArgumentException("Medicine name is required.");
        }
        if (reminderTime == null) {
            throw new IllegalArgumentException("Reminder time is required.");
        }
        User patient = users.findById(patientId).orElseThrow(() -> new NoSuchElementException("Patient not found."));

        MedicineReminder reminder = new MedicineReminder(
                patient, medicineName.trim(),
                (dosage == null || dosage.isBlank()) ? null : dosage.trim(),
                reminderTime,
                (notes == null || notes.isBlank()) ? null : notes.trim()
        );
        return reminders.save(reminder);
    }

    public void setActive(Long id, Long requesterId, boolean active) {
        MedicineReminder reminder = ownedReminder(id, requesterId);
        reminder.setActive(active);
        reminders.save(reminder);
    }

    public void delete(Long id, Long requesterId) {
        MedicineReminder reminder = ownedReminder(id, requesterId);
        reminders.delete(reminder);
    }

    /** Called from AdminUserService.deleteUser(...) before deleting the user row - see that class for why. */
    public void deleteAllForPatient(Long patientId) {
        reminders.deleteByPatientId(patientId);
    }

    private MedicineReminder ownedReminder(Long id, Long requesterId) {
        MedicineReminder reminder = reminders.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reminder not found."));
        if (!reminder.getPatient().getId().equals(requesterId)) {
            throw new IllegalStateException("You can only manage your own reminders.");
        }
        return reminder;
    }
}
