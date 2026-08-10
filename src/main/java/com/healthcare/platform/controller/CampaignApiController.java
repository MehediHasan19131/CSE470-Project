package com.healthcare.platform.controller;

import com.healthcare.platform.dto.CampaignRequest;
import com.healthcare.platform.dto.CampaignResponse;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CampaignService;
import com.healthcare.platform.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 * Campaign APIs.
 */
@RestController
@RequestMapping("/api/campaigns")
public class CampaignApiController {
    private final CampaignService campaignService;
    private final CurrentUserService currentUserService;

    public CampaignApiController(CampaignService campaignService, CurrentUserService currentUserService) {
        this.campaignService = campaignService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<CampaignResponse> getAllCampaigns() {
        return campaignService.getAllCampaigns();
    }

    @GetMapping("/{id}")
    public CampaignResponse getCampaignById(@PathVariable Long id) {
        return campaignService.getCampaignById(id);
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(@Valid @RequestBody CampaignRequest request, Authentication authentication) {
        User organizer = currentUserService.get(authentication);
        CampaignResponse created = campaignService.createCampaign(request, organizer);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public CampaignResponse updateCampaign(@PathVariable Long id, @Valid @RequestBody CampaignRequest request) {
        return campaignService.updateCampaign(id, request);
    }

    @PostMapping("/{id}/close")
    public CampaignResponse closeCampaign(@PathVariable Long id) {
        return campaignService.closeCampaign(id);
    }
}
