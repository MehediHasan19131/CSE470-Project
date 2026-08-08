package com.healthcare.platform.service;

import com.healthcare.platform.dto.DonationRequest;
import com.healthcare.platform.dto.DonationResponse;
import com.healthcare.platform.model.Campaign;
import com.healthcare.platform.model.Donation;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.DonationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 * Payment APIs: processes a (simulated/sandbox) payment for each donation.
 * See the seeded app_settings row "payment_gateway" = "sandbox" - this service
 * always marks the simulated payment as SUCCESS and generates a transaction id,
 * without calling any real external payment provider.
 */
@Service
public class DonationService {
    private final DonationRepository donations;
    private final CampaignService campaignService;

    public DonationService(DonationRepository donations, CampaignService campaignService) {
        this.donations = donations;
        this.campaignService = campaignService;
    }

    public DonationResponse donate(User donor, Long campaignId, DonationRequest request) {
        Campaign campaign = campaignService.getCampaignEntity(campaignId);

        if (!"ACTIVE".equals(campaign.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This campaign is closed and no longer accepting donations");
        }

        // Payment APIs: simulate a successful sandbox payment.
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Donation donation = new Donation(
                campaign,
                donor,
                request.amount(),
                request.message(),
                request.paymentMethod(),
                transactionId,
                "SUCCESS"
        );
        donation = donations.save(donation);
        return DonationResponse.from(donation);
    }

    public List<DonationResponse> getDonationsForUser(Long userId) {
        return donations.findByDonorIdOrderByCreatedAtDesc(userId).stream()
                .map(DonationResponse::from)
                .toList();
    }

    public List<DonationResponse> getDonationsForCampaign(Long campaignId) {
        return donations.findByCampaignIdOrderByCreatedAtDesc(campaignId).stream()
                .map(DonationResponse::from)
                .toList();
    }
}
