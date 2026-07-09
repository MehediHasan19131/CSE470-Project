package com.healthcare.platform.repository;

import com.healthcare.platform.model.HospitalAvailability;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalAvailabilityRepository extends JpaRepository<HospitalAvailability, Long> {
    Optional<HospitalAvailability> findByHospitalId(Long hospitalId);
}
