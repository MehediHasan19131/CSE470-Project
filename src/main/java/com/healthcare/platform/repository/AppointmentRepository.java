package com.healthcare.platform.repository;

import com.healthcare.platform.model.Appointment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdOrderByScheduledAtAsc(Long doctorId);

    List<Appointment> findByPatientIdOrderByScheduledAtAsc(Long patientId);

    long countByDoctorIdAndStatus(Long doctorId, String status);
}
