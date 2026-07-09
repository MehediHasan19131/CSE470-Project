package com.healthcare.platform.repository;

import com.healthcare.platform.model.HospitalService;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalServiceRepository extends JpaRepository<HospitalService, Long> {
    List<HospitalService> findByHospitalIdOrderByServiceNameAsc(Long hospitalId);
}
