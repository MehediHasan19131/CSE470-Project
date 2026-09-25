package com.healthcare.platform.service.sprint2;

import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.service.NotificationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository appointments;
    private final UserRepository users;
    private final NotificationService notificationService;
    private static final int DAILY_APPOINTMENT_CAP = 12;

    public AppointmentService(AppointmentRepository appointments, UserRepository users, NotificationService notificationService) {
        this.appointments = appointments;
        this.users = users;
        this.notificationService = notificationService;
    }

    public Appointment book(User patient, Long doctorId, LocalDateTime scheduledAt, String reason, String visitType) {
        User doctor = users.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        if (doctor.getRole() != UserRole.DOCTOR) throw new IllegalArgumentException("Selected provider is not a doctor");
        LocalDate day = scheduledAt.toLocalDate();
        long booked = appointments.countByDoctorIdAndScheduledAtBetweenAndStatusNot(doctorId,
                day.atStartOfDay(), day.plusDays(1).atStartOfDay(), "cancelled");
        if (booked >= DAILY_APPOINTMENT_CAP) throw new IllegalStateException("This doctor's schedule is full for the selected day. Please choose another date.");
        Appointment apt = new Appointment();
        apt.setPatient(patient);
        apt.setDoctor(doctor);
        apt.setScheduledAt(scheduledAt);
        apt.setReason(reason);
        apt.setVisitType("TELEMEDICINE".equalsIgnoreCase(visitType) ? "TELEMEDICINE" : "IN_PERSON");
        apt.setStatus("pending");
        Appointment saved = appointments.save(apt);
        notificationService.createNotification(doctor, "New appointment request",
                patient.getFullName() + " requested a " + ("TELEMEDICINE".equals(saved.getVisitType()) ? "telemedicine visit" : "in-person visit") + ".",
                "APPOINTMENT_REQUEST", saved.getId());
        return saved;
    }

    public Appointment cancel(Long appointmentId, User currentUser) {
        Appointment apt = appointments.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (!apt.getPatient().getId().equals(currentUser.getId())
                && !currentUser.getRole().equals(UserRole.ADMIN)
                && !apt.getDoctor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Not authorized to cancel this appointment");
        }
        apt.setStatus("cancelled");
        return appointments.save(apt);
    }

    public Appointment confirm(Long appointmentId, User currentUser) {
        Appointment apt = appointments.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        if (!apt.getDoctor().getId().equals(currentUser.getId())
                && !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new RuntimeException("Not authorized to confirm this appointment");
        }
        apt.setStatus("confirmed");
        Appointment saved = appointments.save(apt);
        notificationService.createNotification(apt.getPatient(), "Appointment confirmed",
                "Dr. " + apt.getDoctor().getFullName() + " confirmed your appointment.", "APPOINTMENT_CONFIRMED", apt.getId());
        return saved;
    }

    public List<Appointment> patientHistory(Long patientId) {
        return appointments.findByPatientIdOrderByScheduledAtDesc(patientId);
    }

    public List<Appointment> doctorHistory(Long doctorId) {
        return appointments.findByDoctorIdOrderByScheduledAtDesc(doctorId);
    }

    public Appointment getById(Long id) {
        return appointments.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
}
