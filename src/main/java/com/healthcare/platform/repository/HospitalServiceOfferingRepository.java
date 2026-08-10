package com.healthcare.platform.repository;

import com.healthcare.platform.model.HospitalServiceOffering;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalServiceOfferingRepository extends JpaRepository<HospitalServiceOffering, Long> {
    List<HospitalServiceOffering> findByHospitalIdOrderByServiceName(Long hospitalId);

    List<HospitalServiceOffering> findByHospitalIdAndAvailableTrueOrderByServiceName(Long hospitalId);
}
