package com.healthcare.platform.service;

import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notifications Module (Sprint 3) - Imtiaz Zaman Sami (23101551)
 * Appointment Reminder: periodically scans upcoming appointments (next 24
 * hours) and sends each patient a reminder notification (+ email), once per
 * appointment.
 */
@Component
public class AppointmentReminderScheduler {
    private static final Logger log = LoggerFactory.getLogger(AppointmentReminderScheduler.class);
    private static final String REMINDER_TYPE = "APPOINTMENT_REMINDER";
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final AppointmentRepository appointments;
    private final NotificationRepository notifications;
    private final NotificationService notificationService;

    public AppointmentReminderScheduler(
            AppointmentRepository appointments,
            NotificationRepository notifications,
            NotificationService notificationService
    ) {
        this.appointments = appointments;
        this.notifications = notifications;
        this.notificationService = notificationService;
    }

    // Runs automatically every 5 minutes.
    @Transactional
    @Scheduled(fixedRate = 300000)
    public void sendUpcomingAppointmentReminders() {
        int sent = runReminderCheck();
        if (sent > 0) {
            log.info("Appointment Reminder job sent {} reminder(s)", sent);
        }
    }

    // Also callable directly (e.g. from an admin-only endpoint) so reminders
    // can be demonstrated immediately instead of waiting for the schedule.
    @Transactional
    public int runReminderCheck() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusHours(24);
        int sentCount = 0;

        List<Appointment> all = appointments.findAll();
        for (Appointment appointment : all) {
            if (appointment.getScheduledAt() == null || appointment.getPatient() == null) {
                continue;
            }
            String status = appointment.getStatus() == null ? "" : appointment.getStatus().toLowerCase();
            if (status.equals("cancelled") || status.equals("completed")) {
                continue;
            }
            boolean withinWindow = appointment.getScheduledAt().isAfter(now) && appointment.getScheduledAt().isBefore(windowEnd);
            if (!withinWindow) {
                continue;
            }
            if (notifications.existsByRelatedAppointmentIdAndType(appointment.getId(), REMINDER_TYPE)) {
                continue;
            }

            String doctorName = appointment.getDoctor() != null ? appointment.getDoctor().getFullName() : "your doctor";
            String message = "Reminder: you have an appointment with " + doctorName
                    + " on " + appointment.getScheduledAt().format(DISPLAY_FORMAT) + ".";

            notificationService.createNotification(
                    appointment.getPatient(),
                    "Upcoming Appointment Reminder",
                    message,
                    REMINDER_TYPE,
                    appointment.getId()
            );
            sentCount++;
        }
        return sentCount;
    }
}
