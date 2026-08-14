package com.healthcare.platform.controller;

import com.healthcare.platform.dto.AmbulanceAvailabilityUpdateRequest;
import com.healthcare.platform.dto.AmbulanceLocationUpdateRequest;
import com.healthcare.platform.dto.AmbulanceRequestBookingRequest;
import com.healthcare.platform.dto.AmbulanceRequestResponse;
import com.healthcare.platform.dto.AmbulanceRequestStatusUpdateRequest;
import com.healthcare.platform.dto.AmbulanceResponse;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.AmbulanceService;
import com.healthcare.platform.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sprint 2 — Member 4 (Ambulance Service + Map).
 * Backend: Request Ambulance, Track Ambulance.
 */
@RestController
public class AmbulanceApiController {
    private final AmbulanceService ambulanceService;
    private final CurrentUserService currentUserService;

    public AmbulanceApiController(AmbulanceService ambulanceService, CurrentUserService currentUserService) {
        this.ambulanceService = ambulanceService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/api/ambulances")
    public List<AmbulanceResponse> list(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Boolean availableOnly
    ) {
        return ambulanceService.list(lat, lng, availableOnly);
    }

    @GetMapping("/api/ambulances/mine")
    public List<AmbulanceResponse> myFleet(Authentication authentication) {
        return ambulanceService.myFleet(currentUserService.get(authentication));
    }

    @PatchMapping("/api/ambulances/{ambulanceId}/location")
    public AmbulanceResponse updateLocation(
            @PathVariable Long ambulanceId,
            @Valid @RequestBody AmbulanceLocationUpdateRequest body,
            Authentication authentication
    ) {
        User provider = currentUserService.get(authentication);
        return ambulanceService.updateLocation(provider, ambulanceId, body.latitude(), body.longitude());
    }

    @PatchMapping("/api/ambulances/{ambulanceId}/availability")
    public AmbulanceResponse updateAvailability(
            @PathVariable Long ambulanceId,
            @Valid @RequestBody AmbulanceAvailabilityUpdateRequest body,
            Authentication authentication
    ) {
        User provider = currentUserService.get(authentication);
        return ambulanceService.updateAvailability(provider, ambulanceId, body.available());
    }

    @PostMapping("/api/ambulance-requests")
    public AmbulanceRequestResponse book(@Valid @RequestBody AmbulanceRequestBookingRequest body, Authentication authentication) {
        User patient = currentUserService.get(authentication);
        return ambulanceService.book(patient, body);
    }

    @GetMapping("/api/ambulance-requests/me")
    public List<AmbulanceRequestResponse> myRequests(Authentication authentication) {
        return ambulanceService.myRequests(currentUserService.get(authentication));
    }

    @GetMapping("/api/ambulance-requests/incoming")
    public List<AmbulanceRequestResponse> incoming(Authentication authentication) {
        return ambulanceService.incoming(currentUserService.get(authentication));
    }

    @GetMapping("/api/ambulance-requests/{requestId}")
    public AmbulanceRequestResponse track(@PathVariable Long requestId, Authentication authentication) {
        User actor = currentUserService.get(authentication);
        return ambulanceService.track(actor, requestId);
    }

    @PatchMapping("/api/ambulance-requests/{requestId}/status")
    public AmbulanceRequestResponse updateStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody AmbulanceRequestStatusUpdateRequest body,
            Authentication authentication
    ) {
        User provider = currentUserService.get(authentication);
        return ambulanceService.updateRequestStatus(provider, requestId, body.status());
    }

    @PatchMapping("/api/ambulance-requests/{requestId}/cancel")
    public AmbulanceRequestResponse cancel(@PathVariable Long requestId, Authentication authentication) {
        User patient = currentUserService.get(authentication);
        return ambulanceService.cancelMyRequest(patient, requestId);
    }
}
