package com.healthcare.platform.controller;

import com.healthcare.platform.dto.DonationRequest;
import com.healthcare.platform.dto.DonationResponse;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.DonationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 * Payment APIs (donations trigger a simulated sandbox payment).
 */
@RestController
public class DonationApiController {
    private final DonationService donationService;
    private final CurrentUserService currentUserService;

    public DonationApiController(DonationService donationService, CurrentUserService currentUserService) {
        this.donationService = donationService;
        this.currentUserService = currentUserService;
    }

    // Place a donation (processes the simulated payment)
    @PostMapping("/api/campaigns/{id}/donations")
    public ResponseEntity<DonationResponse> donate(
            @PathVariable Long id,
            @Valid @RequestBody DonationRequest request,
            Authentication authentication
    ) {
        User donor = currentUserService.get(authentication);
        DonationResponse response = donationService.donate(donor, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Donations for a specific campaign
    @GetMapping("/api/campaigns/{id}/donations")
    public List<DonationResponse> getDonationsForCampaign(@PathVariable Long id) {
        return donationService.getDonationsForCampaign(id);
    }

    // My donation history
    @GetMapping("/api/donations")
    public List<DonationResponse> getMyDonations(Authentication authentication) {
        User donor = currentUserService.get(authentication);
        return donationService.getDonationsForUser(donor.getId());
    }
}
