package com.healthcare.platform.repository;

import com.healthcare.platform.model.Medicine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByActiveTrueOrderByNameAsc();

    // Pharmacy Service Module (Sprint 2 - Imtiaz Zaman Sami): Medicine Search
    List<Medicine> findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(String name);
}
