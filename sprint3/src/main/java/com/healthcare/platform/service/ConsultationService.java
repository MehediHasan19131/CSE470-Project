package com.healthcare.platform.service;

import com.healthcare.platform.dto.ConsultationResponse;
import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.model.Consultation;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.ConsultationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Sprint 3 — Member 3 (Telemedicine).
 * A consultation is created from a confirmed Appointment and holds a unique Jitsi Meet room name.
 * Starting a consultation for the same appointment twice reuses the existing room (idempotent),
 * so both the patient and the doctor land in the same call when they each hit "Join".
 */
@Service
public class ConsultationService {
    private final ConsultationRepository consultations;
    private final AppointmentRepository appointments;

    public ConsultationService(ConsultationRepository consultations, AppointmentRepository appointments) {
        this.consultations = consultations;
        this.appointments = appointments;
    }

    public ConsultationResponse start(User actor, Long appointmentId) {
        Appointment appointment = appointments.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        boolean isPatient = appointment.getPatient().getId().equals(actor.getId());
        boolean isDoctor = appointment.getDoctor().getId().equals(actor.getId());
        if (!isPatient && !isDoctor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your appointment");
        }
        if (!"confirmed".equalsIgnoreCase(appointment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The appointment must be confirmed before starting a video consultation");
        }

        Consultation consultation = consultations.findByAppointmentId(appointmentId).orElseGet(() -> {
            Consultation created = new Consultation();
            created.setAppointment(appointment);
            created.setPatient(appointment.getPatient());
            created.setDoctor(appointment.getDoctor());
            created.setScheduledAt(appointment.getScheduledAt());
            created.setRoomName(generateRoomName(appointmentId));
            created.setStatus("SCHEDULED");
            return consultations.save(created);
        });

        return ConsultationResponse.from(consultation);
    }

    public List<ConsultationResponse> myConsultations(User user) {
        List<Consultation> list = switch (user.getRole()) {
            case PATIENT -> consultations.findByPatientIdOrderByScheduledAtDesc(user.getId());
            case DOCTOR -> consultations.findByDoctorIdOrderByScheduledAtDesc(user.getId());
            default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only patients and doctors have consultations");
        };
        return list.stream().map(ConsultationResponse::from).toList();
    }

    public ConsultationResponse get(User actor, Long id) {
        Consultation consultation = findAccessible(actor, id);
        return ConsultationResponse.from(consultation);
    }

    public ConsultationResponse updateStatus(User actor, Long id, String status) {
        Consultation consultation = findAccessible(actor, id);
        String normalized = status.toUpperCase();
        if (!List.of("IN_PROGRESS", "COMPLETED", "CANCELLED").contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        if ("IN_PROGRESS".equals(normalized) && consultation.getStartedAt() == null) {
            consultation.setStartedAt(LocalDateTime.now());
        }
        if ("COMPLETED".equals(normalized)) {
            consultation.setEndedAt(LocalDateTime.now());
            if (consultation.getStartedAt() == null) {
                consultation.setStartedAt(consultation.getEndedAt());
            }
        }
        consultation.setStatus(normalized);
        return ConsultationResponse.from(consultations.save(consultation));
    }

    public ConsultationResponse updateNotes(User doctor, Long id, String notes, String prescription) {
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the consulting doctor can add notes");
        }
        Consultation consultation = consultations.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation not found"));
        if (!consultation.getDoctor().getId().equals(doctor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your consultation");
        }
        consultation.setNotes(notes);
        consultation.setPrescription(prescription);
        return ConsultationResponse.from(consultations.save(consultation));
    }

    private Consultation findAccessible(User actor, Long id) {
        Consultation consultation = consultations.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consultation not found"));
        boolean isPatient = consultation.getPatient().getId().equals(actor.getId());
        boolean isDoctor = consultation.getDoctor().getId().equals(actor.getId());
        if (!isPatient && !isDoctor && actor.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your consultation");
        }
        return consultation;
    }

    private String generateRoomName(Long appointmentId) {
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return "SmartCare-Consult-" + appointmentId + "-" + token;
    }
}
