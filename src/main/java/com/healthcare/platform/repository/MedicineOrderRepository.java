package com.healthcare.platform.repository;

import com.healthcare.platform.model.MedicineOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineOrderRepository extends JpaRepository<MedicineOrder, Long> {
    List<MedicineOrder> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<MedicineOrder> findByPharmacyIdOrderByCreatedAtDesc(Long pharmacyId);
}
