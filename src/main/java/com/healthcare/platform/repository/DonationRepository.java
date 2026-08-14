package com.healthcare.platform.repository;

import com.healthcare.platform.model.Donation;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByDonorIdOrderByCreatedAtDesc(Long donorId);

    List<Donation> findByCampaignIdOrderByCreatedAtDesc(Long campaignId);

    @Query("select coalesce(sum(d.amount), 0) from Donation d where d.campaign.id = :campaignId and d.paymentStatus = 'SUCCESS'")
    BigDecimal sumSuccessfulAmountByCampaignId(@Param("campaignId") Long campaignId);

    long countByCampaignIdAndPaymentStatus(Long campaignId, String paymentStatus);
}
