package com.healthcare.platform.repository;

import com.healthcare.platform.model.Consultation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByPatientIdOrderByScheduledAtDesc(Long patientId);

    List<Consultation> findByDoctorIdOrderByScheduledAtDesc(Long doctorId);

    Optional<Consultation> findByAppointmentId(Long appointmentId);
}
