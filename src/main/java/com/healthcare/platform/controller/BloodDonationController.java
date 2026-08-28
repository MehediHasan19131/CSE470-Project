package com.healthcare.platform.controller;

import com.healthcare.platform.model.BloodRequest;
import com.healthcare.platform.model.DonorUrgency;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.BloodDonationService;
import com.healthcare.platform.service.CurrentUserService;
import java.time.LocalDate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/blood")
public class BloodDonationController {
    private final BloodDonationService bloodDonationService;
    private final CurrentUserService currentUserService;

    public BloodDonationController(BloodDonationService bloodDonationService, CurrentUserService currentUserService) {
        this.bloodDonationService = bloodDonationService; this.currentUserService = currentUserService;
    }

    @GetMapping("/donors")
    public String donors(@RequestParam(required = false) String bloodGroup, @RequestParam(required = false) String city,
                         Authentication authentication, Model model) {
        addUser(authentication, model); model.addAttribute("bloodGroups", BloodDonationService.BLOOD_GROUPS);
        model.addAttribute("selectedBloodGroup", bloodGroup); model.addAttribute("selectedCity", city);
        model.addAttribute("donors", bloodDonationService.donors(bloodGroup, city));
        return "blood-donors";
    }

    @GetMapping("/donors/new")
    public String donorForm(Authentication authentication, Model model) {
        addUser(authentication, model); model.addAttribute("bloodGroups", BloodDonationService.BLOOD_GROUPS);
        return "blood-donor-form";
    }

    @PostMapping("/donors")
    public String createDonor(Authentication authentication, @RequestParam String fullName, @RequestParam String bloodGroup,
                              @RequestParam String phone, @RequestParam(required = false) String city,
                              @RequestParam(required = false) String lastDonationDate, @RequestParam(required = false) String available,
                              RedirectAttributes flash) {
        try {
            bloodDonationService.registerDonor(currentUserService.get(authentication), fullName, bloodGroup, phone, city,
                    lastDonationDate == null || lastDonationDate.isBlank() ? null : LocalDate.parse(lastDonationDate), "on".equals(available));
            flash.addFlashAttribute("success", "You have been registered as a blood donor.");
            return "redirect:/blood/donors";
        } catch (IllegalArgumentException exception) {
            flash.addFlashAttribute("error", exception.getMessage()); return "redirect:/blood/donors/new";
        }
    }

    @GetMapping("/requests")
    public String requests(Authentication authentication, Model model) {
        addUser(authentication, model); model.addAttribute("requests", bloodDonationService.openRequests());
        return "blood-requests";
    }

    @GetMapping("/requests/new")
    public String requestForm(Authentication authentication, Model model) {
        User user = addUser(authentication, model); model.addAttribute("bloodGroups", BloodDonationService.BLOOD_GROUPS);
        model.addAttribute("urgencies", DonorUrgency.values()); model.addAttribute("defaultName", user.getFullName());
        model.addAttribute("defaultPhone", user.getPhone()); model.addAttribute("today", LocalDate.now());
        return "blood-request-form";
    }

    @PostMapping("/requests")
    public String createRequest(Authentication authentication, @RequestParam String requesterName, @RequestParam String requesterPhone,
                                @RequestParam String bloodGroup, @RequestParam int units, @RequestParam(required = false) String location,
                                @RequestParam DonorUrgency urgency, @RequestParam LocalDate neededBy, RedirectAttributes flash) {
        try {
            bloodDonationService.createRequest(currentUserService.get(authentication), requesterName, requesterPhone, bloodGroup, units, location, urgency, neededBy);
            flash.addFlashAttribute("success", "Blood request created successfully."); return "redirect:/blood/requests";
        } catch (IllegalArgumentException exception) {
            flash.addFlashAttribute("error", exception.getMessage()); return "redirect:/blood/requests/new";
        }
    }

    @GetMapping("/requests/{id}")
    public String requestDetails(@PathVariable Long id, Authentication authentication, Model model) {
        addUser(authentication, model); BloodRequest request = bloodDonationService.getRequest(id);
        model.addAttribute("request", request); model.addAttribute("matchingDonors", bloodDonationService.donors(request.getBloodGroupNeeded(), null));
        return "blood-request-detail";
    }

    @PostMapping("/requests/{id}/fulfil")
    public String fulfil(@PathVariable Long id, Authentication authentication, RedirectAttributes flash) {
        try { bloodDonationService.fulfil(id, currentUserService.get(authentication)); flash.addFlashAttribute("success", "Request marked as fulfilled."); }
        catch (IllegalArgumentException exception) { flash.addFlashAttribute("error", exception.getMessage()); }
        return "redirect:/blood/requests/" + id;
    }

    private User addUser(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication); model.addAttribute("user", user); return user;
    }
}
