package com.healthcare.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.healthcare.platform.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);

    // Appointment & Service Booking (Sprint 2 - Rony Miah): history views list
    // newest first.
    List<Appointment> findByPatientIdOrderByScheduledAtDesc(Long patientId);
    List<Appointment> findByDoctorIdOrderByScheduledAtDesc(Long doctorId);
}
