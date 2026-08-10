package com.healthcare.platform.repository;

import com.healthcare.platform.model.Campaign;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findAllByOrderByCreatedAtDesc();

    List<Campaign> findByStatusOrderByCreatedAtDesc(String status);
}
