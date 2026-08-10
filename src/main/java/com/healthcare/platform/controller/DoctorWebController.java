package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.dto.DoctorResponse;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.DoctorService;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Doctor & Patient Module (Sprint 1) - Imtiaz Zaman Sami (23101551)
 * Server-rendered (Thymeleaf + Bootstrap) pages, styled with the shared
 * fragments/dashboard-layout used across the rest of SmartCare:
 * - Doctor List      -> /doctors
 * - Doctor Profile   -> /doctors/{id}
 * - Search Interface -> /doctors/search
 */
@Controller
public class DoctorWebController {
    private final DoctorService doctorService;
    private final CurrentUserService currentUserService;

    public DoctorWebController(DoctorService doctorService, CurrentUserService currentUserService) {
        this.doctorService = doctorService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/doctors")
    public String doctorList(Authentication authentication, Model model) {
        addCurrentUser(authentication, model);
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "doctors-list";
    }

    @GetMapping("/doctors/{id}")
    public String doctorProfile(@PathVariable Long id, Authentication authentication, Model model) {
        addCurrentUser(authentication, model);
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        return "doctor-detail";
    }

    @GetMapping("/doctors/search")
    public String searchInterface(@RequestParam(required = false) String specialty, Authentication authentication, Model model) {
        addCurrentUser(authentication, model);

        boolean searched = specialty != null && !specialty.isBlank();
        List<DoctorResponse> allDoctors = doctorService.getAllDoctors();
        List<DoctorResponse> results = searched ? doctorService.searchBySpecialty(specialty) : List.of();

        List<String> specialties = allDoctors.stream()
                .map(DoctorResponse::specialization)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();

        model.addAttribute("results", results);
        model.addAttribute("searched", searched);
        model.addAttribute("specialty", specialty);
        model.addAttribute("specialties", specialties);
        return "doctors-search";
    }

    private void addCurrentUser(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
    }
}
