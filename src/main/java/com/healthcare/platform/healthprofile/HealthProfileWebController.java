package com.healthcare.platform.healthprofile;

import com.healthcare.platform.auth.AuthUser;
import com.healthcare.platform.auth.AuthUserJdbcRepository;
import com.healthcare.platform.service.RecordAccessService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.NoSuchElementException;

/**
 * Server-rendered "Health Profile Page" (Member 1, Sprint 3 Frontend task).
 * One page, {@code /health-profile}, holds both database pieces at once - a
 * Medical History panel and an Allergies panel, each with its own list +
 * add/edit form - the same way {@code review-target.html} combines Rating
 * Display and Review Form on one page.
 * <p>
 * Editing is handled with a query-string "edit mode" ({@code ?editHistory=id}
 * or {@code ?editAllergy=id}) rather than a separate page per entry, so the
 * whole feature stays on the one URL the task asks for instead of growing
 * into an admin-style list-page + form-page pair. Delete is a small extra
 * beyond "Add"/"Update" - a patient occasionally needs to remove a
 * mis-entered record - mirroring how Sprint 2 added a "browse providers"
 * page beyond its two assigned frontend items.
 */
@Controller
public class HealthProfileWebController {

    private final HealthProfileService healthProfileService;
    private final AuthUserJdbcRepository authUsers;
    private final RecordAccessService recordAccessService;

    public HealthProfileWebController(HealthProfileService healthProfileService, AuthUserJdbcRepository authUsers,
                                       RecordAccessService recordAccessService) {
        this.healthProfileService = healthProfileService;
        this.authUsers = authUsers;
        this.recordAccessService = recordAccessService;
    }

    @GetMapping("/health-profile")
    public String show(@RequestParam(required = false) Long editHistory,
                        @RequestParam(required = false) Long editAllergy,
                        Authentication authentication, Model model) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        loadPage(me, model);

        if (editHistory != null) {
            healthProfileService.getHistory(me.getId()).stream()
                    .filter(h -> h.getId().equals(editHistory))
                    .findFirst()
                    .ifPresent(h -> model.addAttribute("editHistoryEntry", h));
        }
        if (editAllergy != null) {
            healthProfileService.getAllergies(me.getId()).stream()
                    .filter(a -> a.getId().equals(editAllergy))
                    .findFirst()
                    .ifPresent(a -> model.addAttribute("editAllergyEntry", a));
        }
        return "health-profile";
    }

    @PostMapping("/health-profile/history")
    public String addHistory(@RequestParam String condition,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate diagnosedOn,
                              @RequestParam(required = false) String notes,
                              Authentication authentication, Model model) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            healthProfileService.addHistory(me.getId(), condition, diagnosedOn, notes);
            return "redirect:/health-profile?historySaved=true";
        } catch (IllegalArgumentException e) {
            loadPage(me, model);
            model.addAttribute("historyError", e.getMessage());
            return "health-profile";
        }
    }

    @PostMapping("/health-profile/history/{id}")
    public String updateHistory(@PathVariable Long id,
                                 @RequestParam String condition,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate diagnosedOn,
                                 @RequestParam(required = false) String notes,
                                 Authentication authentication, Model model) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            healthProfileService.updateHistory(id, me.getId(), condition, diagnosedOn, notes);
            return "redirect:/health-profile?historySaved=true";
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            loadPage(me, model);
            model.addAttribute("historyError", e.getMessage());
            return "health-profile";
        }
    }

    @PostMapping("/health-profile/history/{id}/delete")
    public String deleteHistory(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            healthProfileService.deleteHistory(id, me.getId());
            return "redirect:/health-profile?historyDeleted=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/health-profile?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/health-profile/allergies")
    public String addAllergy(@RequestParam String allergen,
                              @RequestParam AllergySeverity severity,
                              @RequestParam(required = false) String reaction,
                              Authentication authentication, Model model) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            healthProfileService.addAllergy(me.getId(), allergen, severity, reaction);
            return "redirect:/health-profile?allergySaved=true";
        } catch (IllegalArgumentException e) {
            loadPage(me, model);
            model.addAttribute("allergyError", e.getMessage());
            return "health-profile";
        }
    }

    @PostMapping("/health-profile/allergies/{id}")
    public String updateAllergy(@PathVariable Long id,
                                 @RequestParam String allergen,
                                 @RequestParam AllergySeverity severity,
                                 @RequestParam(required = false) String reaction,
                                 Authentication authentication, Model model) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            healthProfileService.updateAllergy(id, me.getId(), allergen, severity, reaction);
            return "redirect:/health-profile?allergySaved=true";
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            loadPage(me, model);
            model.addAttribute("allergyError", e.getMessage());
            return "health-profile";
        }
    }

    @PostMapping("/health-profile/allergies/{id}/delete")
    public String deleteAllergy(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            healthProfileService.deleteAllergy(id, me.getId());
            return "redirect:/health-profile?allergyDeleted=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/health-profile?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    // ---------------------------------------------------------------------
    // Medical Records Sharing - grant/revoke a specific doctor's access to
    // this patient's medical history & allergies. See RecordAccessService and
    // DoctorPatientRecordsController (the doctor's side of this feature).
    // ---------------------------------------------------------------------

    @PostMapping("/health-profile/share")
    public String share(@RequestParam Long doctorId, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            recordAccessService.grant(me.getId(), doctorId);
            return "redirect:/health-profile?shareSaved=true";
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return "redirect:/health-profile?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/health-profile/share/{doctorId}/revoke")
    public String revokeShare(@PathVariable Long doctorId, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            recordAccessService.revoke(me.getId(), doctorId);
            return "redirect:/health-profile?shareRevoked=true";
        } catch (NoSuchElementException e) {
            return "redirect:/health-profile?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    private void loadPage(AuthUser me, Model model) {
        model.addAttribute("me", me);
        model.addAttribute("history", healthProfileService.getHistory(me.getId()));
        model.addAttribute("allergies", healthProfileService.getAllergies(me.getId()));
        model.addAttribute("severities", AllergySeverity.values());
        model.addAttribute("doctors", recordAccessService.listDoctors());
        model.addAttribute("grants", recordAccessService.listGrantsForPatient(me.getId()));
    }
}
