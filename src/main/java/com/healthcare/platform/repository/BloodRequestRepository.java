package com.healthcare.platform.repository;

import com.healthcare.platform.model.BloodRequest;
import com.healthcare.platform.model.BloodRequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    List<BloodRequest> findByStatusAndNeededByDateGreaterThanEqualOrderByUrgencyDescCreatedAtDesc(BloodRequestStatus status, java.time.LocalDate date);
}
