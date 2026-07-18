package com.healthcare.platform.repository;

import com.healthcare.platform.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
