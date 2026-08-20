package com.healthcare.platform.repository;

import com.healthcare.platform.model.SupportMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {
    List<SupportMessage> findAllByOrderByCreatedAtDesc();
    List<SupportMessage> findBySenderIdOrderByCreatedAtDesc(Long senderId);
}
