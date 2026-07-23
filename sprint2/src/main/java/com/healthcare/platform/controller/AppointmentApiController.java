package com.healthcare.platform.controller;

import com.healthcare.platform.dto.AppointmentBookingRequest;
import com.healthcare.platform.dto.AppointmentResponse;
import com.healthcare.platform.dto.AppointmentStatusUpdateRequest;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.AppointmentService;
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

/** Sprint 2 — Online Appointment Booking + Appointment Reminder. */
@RestController
public class AppointmentApiController {
    private final AppointmentService appointmentService;
    private final CurrentUserService currentUserService;

    public AppointmentApiController(AppointmentService appointmentService, CurrentUserService currentUserService) {
        this.appointmentService = appointmentService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/api/appointments")
    public AppointmentResponse book(@Valid @RequestBody AppointmentBookingRequest body, Authentication authentication) {
        User patient = currentUserService.get(authentication);
        return appointmentService.book(patient, body);
    }

    @GetMapping("/api/appointments/me")
    public List<AppointmentResponse> myAppointments(Authentication authentication) {
        return appointmentService.myAppointments(currentUserService.get(authentication));
    }

    @GetMapping("/api/appointments/reminders")
    public List<AppointmentResponse> reminders(Authentication authentication) {
        return appointmentService.upcomingReminders(currentUserService.get(authentication));
    }

    @PatchMapping("/api/appointments/{appointmentId}/status")
    public AppointmentResponse updateStatus(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentStatusUpdateRequest body,
            Authentication authentication
    ) {
        User actor = currentUserService.get(authentication);
        return appointmentService.updateStatus(actor, appointmentId, body.status());
    }
}
