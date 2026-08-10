package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.FacilityManagementService;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Hospital & Diagnostic Module (Member 3) - the Hospital-owner's three
 * management tools: Bed availability, Doctor availability, Service
 * availability. One page ({@code /hospital/manage}) holds all three panels,
 * the same "one URL per feature area" pattern {@code /health-profile} uses.
 */
@Controller
public class HospitalManagementController {

    private final FacilityManagementService facilityManagementService;
    private final CurrentUserService currentUserService;

    public HospitalManagementController(FacilityManagementService facilityManagementService,
                                         CurrentUserService currentUserService) {
        this.facilityManagementService = facilityManagementService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/hospital/manage")
    public String manage(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("beds", facilityManagementService.getBeds(user.getId()));
        model.addAttribute("doctorAvailability", facilityManagementService.getDoctorAvailability(user.getId()));
        model.addAttribute("services", facilityManagementService.getServices(user.getId()));
        model.addAttribute("assignableDoctors", facilityManagementService.availableDoctorsToAssign());
        return "hospital-management";
    }

    // -- Beds --------------------------------------------------------------

    @PostMapping("/hospital/beds")
    public String addBed(@RequestParam String wardType, @RequestParam int totalBeds, @RequestParam int availableBeds,
                          Authentication authentication) {
        User user = currentUserService.get(authentication);
        facilityManagementService.addBed(user, wardType, totalBeds, availableBeds);
        return "redirect:/hospital/manage?bedSaved=true";
    }

    @PostMapping("/hospital/beds/{id}")
    public String updateBed(@PathVariable Long id, @RequestParam String wardType,
                             @RequestParam int totalBeds, @RequestParam int availableBeds,
                             Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            facilityManagementService.updateBed(user, id, wardType, totalBeds, availableBeds);
            return "redirect:/hospital/manage?bedSaved=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/hospital/manage?error=" + encode(e.getMessage());
        }
    }

    @PostMapping("/hospital/beds/{id}/delete")
    public String deleteBed(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            facilityManagementService.deleteBed(user, id);
        } catch (IllegalStateException | NoSuchElementException ignored) {
            // Nothing to clean up if it's already gone or not theirs.
        }
        return "redirect:/hospital/manage?bedDeleted=true";
    }

    // -- Doctor availability -------------------------------------------------

    @PostMapping("/hospital/doctor-availability")
    public String addDoctorAvailability(@RequestParam Long doctorId, @RequestParam String dayOfWeek,
                                         @RequestParam String startTime, @RequestParam String endTime,
                                         @RequestParam(required = false) String notes,
                                         Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            facilityManagementService.addDoctorAvailability(user, doctorId, dayOfWeek,
                    LocalTime.parse(startTime), LocalTime.parse(endTime), notes);
            return "redirect:/hospital/manage?doctorAvailabilitySaved=true";
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return "redirect:/hospital/manage?error=" + encode(e.getMessage());
        }
    }

    @PostMapping("/hospital/doctor-availability/{id}/delete")
    public String deleteDoctorAvailability(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            facilityManagementService.deleteDoctorAvailability(user, id);
        } catch (IllegalStateException | NoSuchElementException ignored) {
        }
        return "redirect:/hospital/manage?doctorAvailabilityDeleted=true";
    }

    // -- Services --------------------------------------------------------

    @PostMapping("/hospital/services")
    public String addService(@RequestParam String serviceName, @RequestParam(required = false) String description,
                              @RequestParam(required = false) BigDecimal price, Authentication authentication) {
        User user = currentUserService.get(authentication);
        facilityManagementService.addService(user, serviceName, description, price);
        return "redirect:/hospital/manage?serviceSaved=true";
    }

    @PostMapping("/hospital/services/{id}")
    public String updateService(@PathVariable Long id, @RequestParam String serviceName,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(required = false) BigDecimal price,
                                 @RequestParam(defaultValue = "false") boolean available,
                                 Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            facilityManagementService.updateService(user, id, serviceName, description, price, available);
            return "redirect:/hospital/manage?serviceSaved=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/hospital/manage?error=" + encode(e.getMessage());
        }
    }

    @PostMapping("/hospital/services/{id}/delete")
    public String deleteService(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        try {
            facilityManagementService.deleteService(user, id);
        } catch (IllegalStateException | NoSuchElementException ignored) {
        }
        return "redirect:/hospital/manage?serviceDeleted=true";
    }

    private String encode(String message) {
        return URLEncoder.encode(message == null ? "Something went wrong." : message, StandardCharsets.UTF_8);
    }
}
