package com.healthcare.platform.repository;

import com.healthcare.platform.model.ServiceBooking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    List<ServiceBooking> findByProviderIdOrderByCreatedAtDesc(Long providerId);
    List<ServiceBooking> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
