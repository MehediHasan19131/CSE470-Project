package com.healthcare.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.platform.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
