package com.healthcare.platform.controller;

import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.MedicineReminderService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Medicine Reminder: a patient's own list of daily "take this medicine at
 * this time" reminders. See MedicineReminderScheduler for how these turn
 * into Notifications.
 */
@Controller
public class MedicineReminderWebController {

    private final MedicineReminderService reminderService;
    private final CurrentUserService currentUserService;

    public MedicineReminderWebController(MedicineReminderService reminderService, CurrentUserService currentUserService) {
        this.reminderService = reminderService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/medicine-reminders")
    public String list(Authentication authentication, Model model) {
        User me = currentUserService.get(authentication);
        model.addAttribute("user", me);
        model.addAttribute("reminders", reminderService.listForPatient(me.getId()));
        return "medicine-reminders";
    }

    @PostMapping("/medicine-reminders")
    public String create(@RequestParam String medicineName,
                          @RequestParam(required = false) String dosage,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime reminderTime,
                          @RequestParam(required = false) String notes,
                          Authentication authentication) {
        User me = currentUserService.get(authentication);
        try {
            reminderService.create(me.getId(), medicineName, dosage, reminderTime, notes);
            return "redirect:/medicine-reminders?saved=true";
        } catch (IllegalArgumentException e) {
            return "redirect:/medicine-reminders?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/medicine-reminders/{id}/toggle")
    public String toggle(@PathVariable Long id, @RequestParam boolean active, Authentication authentication) {
        User me = currentUserService.get(authentication);
        try {
            reminderService.setActive(id, me.getId(), active);
            return "redirect:/medicine-reminders";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/medicine-reminders?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/medicine-reminders/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        User me = currentUserService.get(authentication);
        try {
            reminderService.delete(id, me.getId());
            return "redirect:/medicine-reminders?deleted=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/medicine-reminders?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
