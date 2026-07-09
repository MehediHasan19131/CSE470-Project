package com.healthcare.platform.repository;

import com.healthcare.platform.model.HospitalDoctorAvailability;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalDoctorAvailabilityRepository extends JpaRepository<HospitalDoctorAvailability, Long> {
    List<HospitalDoctorAvailability> findByHospitalIdOrderByDoctorNameAsc(Long hospitalId);
}
