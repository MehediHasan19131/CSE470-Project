package com.healthcare.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.platform.model.HospitalDoctorAvailability;

public interface HospitalDoctorAvailabilityRepository extends JpaRepository<HospitalDoctorAvailability, Long> {
    List<HospitalDoctorAvailability> findByHospitalIdOrderByDoctorNameAsc(Long hospitalId);
}
