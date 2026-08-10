package com.healthcare.platform.repository;

import com.healthcare.platform.model.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    // Used by the Appointment Reminder job to avoid sending duplicate reminders
    boolean existsByRelatedAppointmentIdAndType(Long relatedAppointmentId, String type);
}

