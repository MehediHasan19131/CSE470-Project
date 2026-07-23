package com.healthcare.platform.repository;

import com.healthcare.platform.model.Ambulance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
    List<Ambulance> findByProviderIdOrderByIdAsc(Long providerId);

    List<Ambulance> findByAvailableTrueOrderByIdAsc();
}
