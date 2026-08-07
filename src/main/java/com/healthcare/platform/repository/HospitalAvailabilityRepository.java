package com.healthcare.platform.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.platform.model.HospitalAvailability;

public interface HospitalAvailabilityRepository extends JpaRepository<HospitalAvailability, Long> {
    Optional<HospitalAvailability> findByHospitalId(Long hospitalId);
}
