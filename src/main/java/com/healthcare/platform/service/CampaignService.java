package com.healthcare.platform.service;

import com.healthcare.platform.dto.CampaignRequest;
import com.healthcare.platform.dto.CampaignResponse;
import com.healthcare.platform.model.Campaign;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.CampaignRepository;
import com.healthcare.platform.repository.DonationRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 * Campaign APIs.
 */
@Service
public class CampaignService {
    private final CampaignRepository campaigns;
    private final DonationRepository donations;

    public CampaignService(CampaignRepository campaigns, DonationRepository donations) {
        this.campaigns = campaigns;
        this.donations = donations;
    }

    public List<CampaignResponse> getAllCampaigns() {
        return campaigns.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CampaignResponse> getActiveCampaigns() {
        return campaigns.findByStatusOrderByCreatedAtDesc("ACTIVE").stream()
                .map(this::toResponse)
                .toList();
    }

    public CampaignResponse getCampaignById(Long id) {
        return toResponse(getCampaignEntity(id));
    }

    public Campaign getCampaignEntity(Long id) {
        return campaigns.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found with id: " + id));
    }

    public CampaignResponse createCampaign(CampaignRequest request, User organizer) {
        Campaign campaign = new Campaign(
                request.title(),
                request.description(),
                request.category(),
                request.goalAmount(),
                organizer
        );
        campaign = campaigns.save(campaign);
        return toResponse(campaign);
    }

    public CampaignResponse updateCampaign(Long id, CampaignRequest request) {
        Campaign campaign = getCampaignEntity(id);
        campaign.setTitle(request.title());
        campaign.setDescription(request.description());
        campaign.setCategory(request.category());
        campaign.setGoalAmount(request.goalAmount());
        campaign = campaigns.save(campaign);
        return toResponse(campaign);
    }

    public CampaignResponse closeCampaign(Long id) {
        Campaign campaign = getCampaignEntity(id);
        campaign.setStatus("CLOSED");
        campaign = campaigns.save(campaign);
        return toResponse(campaign);
    }

    private CampaignResponse toResponse(Campaign campaign) {
        var raised = donations.sumSuccessfulAmountByCampaignId(campaign.getId());
        var count = donations.countByCampaignIdAndPaymentStatus(campaign.getId(), "SUCCESS");
        return CampaignResponse.from(campaign, raised, count);
    }
}
