package com.healthcare.platform.controller.sprint2;

import com.healthcare.platform.dto.ConsultationResponse;
import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.service.ConsultationService;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.ListingService;
import com.healthcare.platform.service.sprint2.AppointmentService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CurrentUserService currentUserService;
    private final UserRepository users;
    private final ListingService listingService;
    private final ConsultationService consultationService;

    public AppointmentController(AppointmentService appointmentService,
                                  CurrentUserService currentUserService,
                                  UserRepository users,
                                  ListingService listingService,
                                  ConsultationService consultationService) {
        this.appointmentService = appointmentService;
        this.currentUserService = currentUserService;
        this.users = users;
        this.listingService = listingService;
        this.consultationService = consultationService;
    }

    @GetMapping
    public String history(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        List<Appointment> appointments;
        if (user.getRole() == UserRole.ADMIN) {
            appointments = users.findByRole(UserRole.PATIENT).stream()
                    .flatMap(p -> appointmentService.patientHistory(p.getId()).stream())
                    .toList();
        } else if (user.getRole() == UserRole.DOCTOR) {
            appointments = appointmentService.doctorHistory(user.getId());
        } else {
            appointments = appointmentService.patientHistory(user.getId());
        }
        model.addAttribute("user", user);
        model.addAttribute("appointments", appointments);
        return "sprint2/appointments/history";
    }

    @GetMapping("/book")
    public String bookForm(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("doctors", listingService.doctors(null, null));
        return "sprint2/appointments/book";
    }

    @PostMapping("/book")
    public String bookSubmit(
            @RequestParam Long doctorId,
            @RequestParam String scheduledAt,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        User patient = currentUserService.get(authentication);
        appointmentService.book(patient, doctorId, LocalDateTime.parse(scheduledAt), reason);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        appointmentService.cancel(id, user);
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        appointmentService.confirm(id, user);
        return "redirect:/appointments";
    }

    // Telemedicine (Sprint 3 - Mehedi Hasan). Creates (or reuses) the Consultation
    // room for a confirmed appointment and sends whoever clicked straight into the
    // call. Idempotent - both the patient and the doctor can click their own
    // "Start video call" button and land in the same room.
    @PostMapping("/{id}/start-call")
    public String startCall(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        ConsultationResponse consultation = consultationService.start(user, id);
        return "redirect:/telemedicine/call/" + consultation.id();
    }
}
