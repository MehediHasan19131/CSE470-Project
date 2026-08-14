package com.healthcare.platform.controller.sprint3;

import com.healthcare.platform.model.MedicineReminder;
import com.healthcare.platform.model.User;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.sprint3.ReminderService;
import java.time.LocalDate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reminders")
public class ReminderController {

    private final ReminderService reminderService;
    private final CurrentUserService currentUserService;

    public ReminderController(ReminderService reminderService, CurrentUserService currentUserService) {
        this.reminderService = reminderService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("reminders", reminderService.activeFor(user));
        return "sprint3/reminders/dashboard";
    }

    @GetMapping("/new")
    public String newForm(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("reminder", new MedicineReminder());
        return "sprint3/reminders/form";
    }

    @PostMapping("/new")
    public String create(
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam String medicineName,
            @RequestParam String dosage,
            @RequestParam int frequencyPerDay,
            @RequestParam String reminderTimes,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String notes) {
        User user = currentUserService.get(authentication);
        try {
            reminderService.create(user, medicineName, dosage, frequencyPerDay, reminderTimes,
                    parseDate(startDate), parseDate(endDate), notes);
            redirectAttributes.addFlashAttribute("success", "Medicine reminder added.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reminders/new";
        }
        return "redirect:/reminders";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("reminder", reminderService.get(id, user));
        return "sprint3/reminders/form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam String medicineName,
            @RequestParam String dosage,
            @RequestParam int frequencyPerDay,
            @RequestParam String reminderTimes,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String notes) {
        User user = currentUserService.get(authentication);
        try {
            reminderService.update(id, user, medicineName, dosage, frequencyPerDay, reminderTimes,
                    parseDate(startDate), parseDate(endDate), notes);
            redirectAttributes.addFlashAttribute("success", "Medicine reminder updated.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reminders/" + id + "/edit";
        }
        return "redirect:/reminders";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        reminderService.deactivate(id, user);
        return "redirect:/reminders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        User user = currentUserService.get(authentication);
        reminderService.delete(id, user);
        return "redirect:/reminders";
    }

    @PostMapping("/{id}/log")
    public String logDose(@PathVariable Long id, Authentication authentication,
                          RedirectAttributes redirectAttributes,
                          @RequestParam String status,
                          @RequestParam(required = false) String scheduledTime) {
        User user = currentUserService.get(authentication);
        try {
            reminderService.logDose(id, user, status, scheduledTime);
            redirectAttributes.addFlashAttribute("success", "Dose marked as " + status + ".");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reminders";
    }

    @GetMapping("/history")
    public String history(Authentication authentication, Model model) {
        User user = currentUserService.get(authentication);
        model.addAttribute("user", user);
        model.addAttribute("logs", reminderService.history(user));
        return "sprint3/reminders/history";
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }
}
