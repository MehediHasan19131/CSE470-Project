package com.healthcare.platform.repository;

import com.healthcare.platform.model.MedicineLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineLogRepository extends JpaRepository<MedicineLog, Long> {

    @Query("SELECT l FROM MedicineLog l JOIN FETCH l.reminder r WHERE r.patient.id = :patientId ORDER BY l.loggedAt DESC")
    List<MedicineLog> findHistoryByPatientId(@Param("patientId") Long patientId);

    @Query("DELETE FROM MedicineLog l WHERE l.reminder.id = :reminderId")
    @Modifying
    void deleteByReminderId(@Param("reminderId") Long reminderId);
}
