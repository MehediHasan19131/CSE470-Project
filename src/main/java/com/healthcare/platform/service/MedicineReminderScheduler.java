package com.healthcare.platform.service;

import com.healthcare.platform.model.MedicineReminder;
import com.healthcare.platform.repository.MedicineReminderRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Medicine Reminder: periodically checks every active reminder against the
 * current time and, once per day per reminder, sends the patient a
 * Notification (+ email, via NotificationService) - same
 * scan-and-dedupe shape as {@link AppointmentReminderScheduler}, just keyed
 * on {@code lastTriggeredDate} instead of a Notification lookup, since a
 * reminder repeats daily rather than firing once for a single event.
 */
@Component
public class MedicineReminderScheduler {
    private static final Logger log = LoggerFactory.getLogger(MedicineReminderScheduler.class);
    private static final String REMINDER_TYPE = "MEDICINE_REMINDER";

    private final MedicineReminderRepository reminders;
    private final NotificationService notificationService;

    public MedicineReminderScheduler(MedicineReminderRepository reminders, NotificationService notificationService) {
        this.reminders = reminders;
        this.notificationService = notificationService;
    }

    // Runs every minute so a reminder fires within a minute of its set time.
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void sendDueReminders() {
        int sent = runReminderCheck();
        if (sent > 0) {
            log.info("Medicine Reminder job sent {} reminder(s)", sent);
        }
    }

    // Also callable directly (e.g. an admin-only endpoint, same idea as
    // AppointmentReminderScheduler.runReminderCheck()) to demonstrate it
    // immediately instead of waiting up to a minute.
    @Transactional
    public int runReminderCheck() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        int sentCount = 0;

        List<MedicineReminder> active = reminders.findByActiveTrue();
        for (MedicineReminder reminder : active) {
            if (reminder.getPatient() == null || reminder.getReminderTime() == null) {
                continue;
            }
            boolean alreadySentToday = today.equals(reminder.getLastTriggeredDate());
            boolean timeHasArrived = !now.isBefore(reminder.getReminderTime());
            if (alreadySentToday || !timeHasArrived) {
                continue;
            }

            String dosageText = (reminder.getDosage() == null || reminder.getDosage().isBlank())
                    ? "" : " (" + reminder.getDosage() + ")";
            String message = "Time to take " + reminder.getMedicineName() + dosageText + ".";

            notificationService.createNotification(
                    reminder.getPatient(),
                    "Medicine Reminder",
                    message,
                    REMINDER_TYPE,
                    null
            );

            reminder.setLastTriggeredDate(today);
            reminders.save(reminder);
            sentCount++;
        }
        return sentCount;
    }
}
