package com.healthcare.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.platform.model.HospitalService;

public interface HospitalServiceRepository extends JpaRepository<HospitalService, Long> {
    List<HospitalService> findByHospitalIdOrderByServiceNameAsc(Long hospitalId);
}
