package com.healthcare.platform.controller;

import com.healthcare.platform.dto.ConsultationNotesUpdateRequest;
import com.healthcare.platform.dto.ConsultationResponse;
import com.healthcare.platform.dto.ConsultationStartRequest;
import com.healthcare.platform.dto.ConsultationStatusUpdateRequest;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.ConsultationService;
import com.healthcare.platform.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sprint 3 — Member 3 (Telemedicine).
 * Backend: Consultation APIs.
 */
@RestController
public class ConsultationApiController {
    private final ConsultationService consultationService;
    private final CurrentUserService currentUserService;

    public ConsultationApiController(ConsultationService consultationService, CurrentUserService currentUserService) {
        this.consultationService = consultationService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/api/consultations")
    public ConsultationResponse start(@Valid @RequestBody ConsultationStartRequest body, Authentication authentication) {
        User actor = currentUserService.get(authentication);
        return consultationService.start(actor, body.appointmentId());
    }

    @GetMapping("/api/consultations/me")
    public List<ConsultationResponse> myConsultations(Authentication authentication) {
        return consultationService.myConsultations(currentUserService.get(authentication));
    }

    @GetMapping("/api/consultations/{id}")
    public ConsultationResponse get(@PathVariable Long id, Authentication authentication) {
        User actor = currentUserService.get(authentication);
        return consultationService.get(actor, id);
    }

    @PatchMapping("/api/consultations/{id}/status")
    public ConsultationResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ConsultationStatusUpdateRequest body,
            Authentication authentication
    ) {
        User actor = currentUserService.get(authentication);
        return consultationService.updateStatus(actor, id, body.status());
    }

    @PatchMapping("/api/consultations/{id}/notes")
    public ConsultationResponse updateNotes(
            @PathVariable Long id,
            @RequestBody ConsultationNotesUpdateRequest body,
            Authentication authentication
    ) {
        User doctor = currentUserService.get(authentication);
        return consultationService.updateNotes(doctor, id, body.notes(), body.prescription());
    }
}
