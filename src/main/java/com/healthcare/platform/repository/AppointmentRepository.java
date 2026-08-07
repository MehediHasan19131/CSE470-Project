package com.healthcare.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.platform.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdOrderByScheduledAtAsc(Long doctorId);

    List<Appointment> findByPatientIdOrderByScheduledAtAsc(Long patientId);

    long countByDoctorIdAndStatus(Long doctorId, String status);
}
