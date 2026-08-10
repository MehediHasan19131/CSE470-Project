package com.healthcare.platform.repository;

import com.healthcare.platform.model.MedicineReminder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineReminderRepository extends JpaRepository<MedicineReminder, Long> {

    @Query("SELECT r FROM MedicineReminder r WHERE r.patient.id = :patientId AND r.active = true ORDER BY r.createdAt DESC")
    List<MedicineReminder> findActiveByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT r FROM MedicineReminder r WHERE r.patient.id = :patientId ORDER BY r.createdAt DESC")
    List<MedicineReminder> findAllByPatientId(@Param("patientId") Long patientId);
}
