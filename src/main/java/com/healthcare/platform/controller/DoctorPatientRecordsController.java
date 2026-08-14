package com.healthcare.platform.controller;

import com.healthcare.platform.healthprofile.HealthProfileService;
import com.healthcare.platform.model.RecordAccessGrant;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.RecordAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Medical Records Sharing - the doctor's side. A doctor only ever sees
 * patients who currently have an active {@link RecordAccessGrant} for them;
 * {@link #patientRecords} re-checks that grant on every request (not just
 * when building the "My Patients" list), so a patient revoking access locks
 * the doctor out immediately, even via a bookmarked URL.
 */
@Controller
@RequestMapping("/doctor/patients")
public class DoctorPatientRecordsController {

    private final RecordAccessService recordAccessService;
    private final HealthProfileService healthProfileService;
    private final CurrentUserService currentUserService;
    private final UserRepository users;

    public DoctorPatientRecordsController(RecordAccessService recordAccessService,
                                           HealthProfileService healthProfileService,
                                           CurrentUserService currentUserService,
                                           UserRepository users) {
        this.recordAccessService = recordAccessService;
        this.healthProfileService = healthProfileService;
        this.currentUserService = currentUserService;
        this.users = users;
    }

    @GetMapping
    public String myPatients(Authentication authentication, Model model) {
        User me = currentUserService.get(authentication);
        model.addAttribute("user", me);
        model.addAttribute("grants", recordAccessService.listActivePatientsForDoctor(me.getId()));
        return "doctor-patients";
    }

    @GetMapping("/{patientId}")
    public String patientRecords(@PathVariable Long patientId, Authentication authentication, Model model) {
        User me = currentUserService.get(authentication);

        if (!recordAccessService.hasActiveAccess(patientId, me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This patient hasn't shared their records with you (or has revoked access).");
        }

        User patient = users.findById(patientId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found."));

        model.addAttribute("user", me);
        model.addAttribute("patient", patient);
        model.addAttribute("history", healthProfileService.getHistory(patientId));
        model.addAttribute("allergies", healthProfileService.getAllergies(patientId));
        return "doctor-patient-records";
    }
}
