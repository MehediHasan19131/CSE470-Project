package com.healthcare.platform.service.sprint2;

import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// Named explicitly: com.healthcare.platform.service.AppointmentService has the same simple name, and two beans
// sharing the default name "appointmentService" break context startup.
@Service("sprint2AppointmentService")
public class AppointmentService {

    private final AppointmentRepository appointments;
    private final UserRepository users;

    public AppointmentService(AppointmentRepository appointments, UserRepository users) {
        this.appointments = appointments;
        this.users = users;
    }

    public Appointment book(User patient, Long doctorId, LocalDateTime scheduledAt, String reason) {
        User doctor = users.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Appointment apt = new Appointment();
        apt.setPatient(patient);
        apt.setDoctor(doctor);
        apt.setScheduledAt(scheduledAt);
        apt.setReason(reason);
        apt.setStatus("pending");
        return appointments.save(apt);
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
        return appointments.save(apt);
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
