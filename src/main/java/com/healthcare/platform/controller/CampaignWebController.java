package com.healthcare.platform.controller;

import com.healthcare.platform.dto.CampaignResponse;
import com.healthcare.platform.dto.DonationRequest;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CampaignService;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.DonationService;
import java.math.BigDecimal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 * Server-rendered (Thymeleaf + Bootstrap) pages:
 * - Campaign Page  -> /campaigns (list) and /campaigns/{id} (detail + donate form)
 * - Donation Page  -> /donations (my donation history)
 */
@Controller
public class CampaignWebController {
    private final CampaignService campaignService;
    private final DonationService donationService;
    private final CurrentUserService currentUserService;

    public CampaignWebController(CampaignService campaignService, DonationService donationService, CurrentUserService currentUserService) {
        this.campaignService = campaignService;
        this.donationService = donationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/campaigns")
    public String campaignPage(Authentication authentication, Model model) {
        addCurrentUser(authentication, model);
        model.addAttribute("campaigns", campaignService.getActiveCampaigns());
        return "campaigns";
    }

    @GetMapping("/campaigns/{id}")
    public String campaignDetail(@PathVariable Long id, Authentication authentication, Model model) {
        addCurrentUser(authentication, model);
        CampaignResponse campaign = campaignService.getCampaignById(id);
        model.addAttribute("campaign", campaign);
        model.addAttribute("recentDonations", donationService.getDonationsForCampaign(id));
        return "campaign-detail";
    }

    // Place a donation (form submission from the Campaign Detail page)
    @PostMapping("/campaigns/{id}/donate")
    public String donate(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String message,
            @RequestParam String paymentMethod,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        User donor = currentUserService.get(authentication);
        donationService.donate(donor, id, new DonationRequest(amount, message, paymentMethod));
        redirectAttributes.addFlashAttribute("donationPlaced", true);
        return "redirect:/donations";
    }

    @GetMapping("/donations")
    public String donationPage(Authentication authentication, Model model) {
        User user = addCurrentUser(authentication, model);
        model.addAttribute("donations", donationService.getDonationsForUser(user.getId()));
        return "donations";
    }

    private User addCurrentUser(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        return user;
    }
}
