package com.healthcare.platform.controller.sprint4;

import java.util.List;
import java.time.LocalDate;
import org.springframework.security.core.Authentication;
import com.healthcare.platform.model.Donor;
import com.healthcare.platform.model.DonorUrgency;
import com.healthcare.platform.model.BloodRequest;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.sprint4.BloodDonationService;
import com.healthcare.platform.model.BloodRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.healthcare.platform.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/blood")
public class BloodDonationController {

    private final BloodDonationService bloodService;
    private final CurrentUserService currentUserService;

    public BloodDonationController(BloodDonationService bloodService,
                                   CurrentUserService currentUserService) {
        this.bloodService = bloodService;
        this.currentUserService = currentUserService;
    }

    // ---------- Donor Registration ----------
    @GetMapping("/donors/new")
    public String newDonorForm(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("donor", new Donor());
        model.addAttribute("bloodGroups", List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        return "sprint4/donors/form";
    }

    @PostMapping("/donors/new")
    public String createDonor(
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam String fullName,
            @RequestParam String bloodGroup,
            @RequestParam String phone,
            @RequestParam String city,
            @RequestParam(required = false) String lastDonationDate,
            @RequestParam(required = false) Boolean isAvailable) {
        User user = currentUserService.get(authentication);
        try {
            bloodService.create(user, fullName, bloodGroup, phone, city,
                    lastDonationDate != null ? LocalDate.parse(lastDonationDate) : null, isAvailable);
            redirectAttributes.addFlashAttribute("success", "Donor registered.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/blood/donors/new";
        }
        return "redirect:/blood/donors";
    }

    @GetMapping("/donors")
    public String donorDirectory(@RequestParam(required = false) String bloodGroup,
                                 @RequestParam(required = false) String city,
                                 Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("bloodGroup", bloodGroup);
        model.addAttribute("city", city);
        model.addAttribute("donors", bloodService.matchingDonors(bloodGroup, city));
        model.addAttribute("bloodGroups", List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        return "sprint4/donors/directory";
    }

    // ---------- Blood Request ----------
    @GetMapping("/requests")
    public String requestList(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("requests", bloodService.openRequests());
        return "sprint4/requests/list";
    }

    @GetMapping("/requests/new")
    public String newRequestForm(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("request", new BloodRequest());
        model.addAttribute("bloodGroups", List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        model.addAttribute("urgencies", List.of("LOW", "MEDIUM", "HIGH", "CRITICAL"));
        model.addAttribute("neededByDate", LocalDate.now());
        return "sprint4/requests/form";
    }

    @PostMapping("/requests/new")
    public String createRequest(
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam String requesterName,
            @RequestParam String requesterPhone,
            @RequestParam String bloodGroupNeeded,
            @RequestParam int unitsNeeded,
            @RequestParam String urgency,
            @RequestParam String hospitalOrLocation,
            @RequestParam String neededByDate) {
        User user = currentUserService.get(authentication);
        try {
            bloodService.create(requesterName, requesterPhone, bloodGroupNeeded, unitsNeeded,
                    hospitalOrLocation, DonorUrgency.valueOf(urgency.toUpperCase()),
                    LocalDate.parse(neededByDate));
            redirectAttributes.addFlashAttribute("success", "Blood request created.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/blood/requests/new";
        }
        return "redirect:/blood/requests";
    }

    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable Long id, Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        BloodRequest req = bloodService.getRequest(id, user);
        model.addAttribute("user", user);
        model.addAttribute("request", req);
        // Show matching donors, excluding the requester's own donor record
        model.addAttribute("matchingDonors", bloodService.matchingDonors(req.getBloodGroupNeeded(), null, user.getId()));
        return "sprint4/requests/detail";
    }

    @PostMapping("/requests/{id}/fulfill")
    public String fulfill(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = currentUserService.get(authentication);
        bloodService.markFulfilled(id, user);
        redirectAttributes.addFlashAttribute("success", "Request marked as fulfilled.");
        return "redirect:/blood/requests/" + id;
    }

    @PostMapping("/requests/{id}/expire")
    public String expire(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bloodService.markExpired(id);
        redirectAttributes.addFlashAttribute("success", "Request marked as expired.");
        return "redirect:/blood/requests/" + id;
    }
}