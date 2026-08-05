package com.healthcare.platform.repository;

import com.healthcare.platform.model.AmbulanceRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbulanceRequestRepository extends JpaRepository<AmbulanceRequest, Long> {
    List<AmbulanceRequest> findByPatientIdOrderByRequestedAtDesc(Long patientId);

    List<AmbulanceRequest> findByStatusOrderByRequestedAtAsc(String status);

    List<AmbulanceRequest> findByAmbulanceIdInOrderByRequestedAtDesc(List<Long> ambulanceIds);
}
