package com.healthcare.platform.service;

import com.healthcare.platform.dto.AppointmentBookingRequest;
import com.healthcare.platform.dto.AppointmentResponse;
import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Sprint 2 — Appointment Booking (with lightweight in-app reminders). */
// Named explicitly: com.healthcare.platform.service.sprint2.AppointmentService has the same simple name, and two beans
// sharing the default name "appointmentService" break context startup.
@Service("mehediAppointmentService")
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final UserRepository users;
    private final ProfileRepository profiles;

    public AppointmentService(AppointmentRepository appointments, UserRepository users, ProfileRepository profiles) {
        this.appointments = appointments;
        this.users = users;
        this.profiles = profiles;
    }

    public AppointmentResponse book(User patient, AppointmentBookingRequest request) {
        requireRole(patient, UserRole.PATIENT);
        User doctor = users.findById(request.doctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected user is not a doctor");
        }
        if (request.scheduledAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment time must be in the future");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setScheduledAt(request.scheduledAt());
        appointment.setReason(request.reason());
        appointment.setStatus("pending");
        return toResponse(appointments.save(appointment));
    }

    public List<AppointmentResponse> myAppointments(User user) {
        List<Appointment> list = switch (user.getRole()) {
            case PATIENT -> appointments.findByPatientIdOrderByScheduledAtAsc(user.getId());
            case DOCTOR -> appointments.findByDoctorIdOrderByScheduledAtAsc(user.getId());
            default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients and doctors have appointments");
        };
        return list.stream().map(this::toResponse).toList();
    }

    public List<AppointmentResponse> upcomingReminders(User user) {
        return myAppointments(user).stream()
                .filter(AppointmentResponse::reminderDue)
                .toList();
    }

    public AppointmentResponse updateStatus(User actor, Long appointmentId, String status) {
        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        String normalized = status.toLowerCase();
        boolean isDoctor = appointment.getDoctor().getId().equals(actor.getId());
        boolean isPatient = appointment.getPatient().getId().equals(actor.getId());

        if (normalized.equals("cancelled")) {
            if (!isDoctor && !isPatient) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your appointment");
            }
        } else if (normalized.equals("confirmed") || normalized.equals("completed")) {
            if (!isDoctor) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the doctor can confirm or complete an appointment");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        appointment.setStatus(normalized);
        return toResponse(appointments.save(appointment));
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        String specialization = profiles.findByUserId(appointment.getDoctor().getId())
                .map(Profile::getSpecialization)
                .orElse(null);
        return AppointmentResponse.from(appointment, specialization);
    }

    private void requireRole(User user, UserRole role) {
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This action requires the " + role + " role");
        }
    }
}
