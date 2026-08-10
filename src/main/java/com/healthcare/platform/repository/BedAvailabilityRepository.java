package com.healthcare.platform.repository;

import com.healthcare.platform.model.BedAvailability;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BedAvailabilityRepository extends JpaRepository<BedAvailability, Long> {
    List<BedAvailability> findByHospitalIdOrderByWardType(Long hospitalId);
}
