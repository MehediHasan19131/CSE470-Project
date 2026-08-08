package com.healthcare.platform.dto;

import com.healthcare.platform.model.Donation;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 */
public record DonationResponse(
        Long id,
        Long campaignId,
        String campaignTitle,
        String donorName,
        BigDecimal amount,
        String message,
        String paymentMethod,
        String transactionId,
        String paymentStatus,
        LocalDateTime createdAt
) {
    public static DonationResponse from(Donation donation) {
        return new DonationResponse(
                donation.getId(),
                donation.getCampaign() == null ? null : donation.getCampaign().getId(),
                donation.getCampaign() == null ? null : donation.getCampaign().getTitle(),
                donation.getDonor() == null ? null : donation.getDonor().getFullName(),
                donation.getAmount(),
                donation.getMessage(),
                donation.getPaymentMethod(),
                donation.getTransactionId(),
                donation.getPaymentStatus(),
                donation.getCreatedAt()
        );
    }
}
