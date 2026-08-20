package com.healthcare.platform.repository;

import com.healthcare.platform.model.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPayerIdOrderByCreatedAtDesc(Long payerId);
    List<Payment> findAllByOrderByCreatedAtDesc();
}
