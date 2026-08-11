package com.healthcare.platform.repository;

import com.healthcare.platform.model.MedicineReminder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineReminderRepository extends JpaRepository<MedicineReminder, Long> {
    List<MedicineReminder> findByPatientIdOrderByReminderTime(Long patientId);
    List<MedicineReminder> findByActiveTrue();

    // Called from AdminUserService.deleteUser(...) before deleting the user row -
    // medicine_reminders has a foreign key on users.id.
    void deleteByPatientId(Long patientId);
}
