package com.healthcare.platform.dto;

import com.healthcare.platform.model.Campaign;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 */
public record CampaignResponse(
        Long id,
        String title,
        String description,
        String category,
        BigDecimal goalAmount,
        BigDecimal raisedAmount,
        int progressPercent,
        String status,
        String organizerName,
        long donationCount,
        LocalDateTime createdAt
) {
    public static CampaignResponse from(Campaign campaign, BigDecimal raisedAmount, long donationCount) {
        BigDecimal raised = raisedAmount == null ? BigDecimal.ZERO : raisedAmount;
        int percent = 0;
        if (campaign.getGoalAmount() != null && campaign.getGoalAmount().compareTo(BigDecimal.ZERO) > 0) {
            percent = raised
                    .multiply(BigDecimal.valueOf(100))
                    .divide(campaign.getGoalAmount(), 0, RoundingMode.DOWN)
                    .intValue();
            percent = Math.min(percent, 100);
        }
        return new CampaignResponse(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getCategory(),
                campaign.getGoalAmount(),
                raised,
                percent,
                campaign.getStatus(),
                campaign.getOrganizer() == null ? null : campaign.getOrganizer().getFullName(),
                donationCount,
                campaign.getCreatedAt()
        );
    }
}
