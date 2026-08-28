package com.healthcare.platform.controller.admin;
import com.healthcare.platform.service.admin.*;

import com.healthcare.platform.model.auth.AuthUser;
import com.healthcare.platform.repository.auth.AuthUserJdbcRepository;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.service.FaqService;
import com.healthcare.platform.service.review.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Admin-only pages: a small dashboard, and full user CRUD (create/edit/delete
 * any account). Every route here is restricted to ROLE_ADMIN in SecurityConfig
 * (.requestMatchers("/admin/**").hasRole("ADMIN")) - this controller doesn't
 * re-check role itself, Spring Security rejects non-admins before a request
 * even reaches these methods.
 * <p>
 * admin-user-form.html is shared by both "create" and "edit" - rather than
 * branching inside the template, this controller always hands it the same
 * flat set of formXxx attributes (plus formAction, the exact URL to submit
 * to) regardless of which mode it's in, so the template itself stays branch-free
 * for every field except the couple of things that are genuinely mode-specific
 * (password required vs. optional, the active checkbox).
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminUserService adminUserService;
    private final AuthUserJdbcRepository authUsers;
    private final ReviewService reviewService;
    private final FaqService faqService;

    public AdminController(AdminUserService adminUserService, AuthUserJdbcRepository authUsers,
                            ReviewService reviewService, FaqService faqService) {
        this.adminUserService = adminUserService;
        this.authUsers = authUsers;
        this.reviewService = reviewService;
        this.faqService = faqService;
    }

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("me", authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow());

        List<AuthUser> allUsers = adminUserService.listUsers();
        Map<UserRole, Long> roleCounts = allUsers.stream()
                .collect(Collectors.groupingBy(AuthUser::getRole, Collectors.counting()));

        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("roleCounts", roleCounts);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("providers", reviewService.getProviders());
        model.addAttribute("pendingApprovals", adminUserService.listPendingApproval());
        return "admin-dashboard";
    }

    // One-click approve for a Doctor/Hospital/Pharmacy/Diagnostic/Ambulance
    // sign-up - just flips is_active to true so they can log in.
    @PostMapping("/users/{id}/approve")
    public String approveUser(@PathVariable Long id, @RequestParam(required = false) String from) {
        try {
            adminUserService.approveUser(id);
        } catch (NoSuchElementException e) {
            return "redirect:/admin?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
        return "redirect:" + ("users".equals(from) ? "/admin/users?approved=true" : "/admin?approved=true");
    }

    @GetMapping("/users")
    public String listUsers(Authentication authentication, Model model) {
        model.addAttribute("me", authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow());
        model.addAttribute("users", adminUserService.listUsers());
        return "admin-users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Authentication authentication, Model model) {
        model.addAttribute("me", authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow());
        model.addAttribute("mode", "create");
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("formAction", "/admin/users");
        model.addAttribute("formFullName", "");
        model.addAttribute("formEmail", "");
        model.addAttribute("formPhone", "");
        model.addAttribute("formRole", null);
        model.addAttribute("formActive", true);
        return "admin-user-form";
    }

    @PostMapping("/users")
    public String createUser(Authentication authentication,
                              @RequestParam String fullName,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam UserRole role,
                              @RequestParam(required = false) String phone,
                              Model model) {
        try {
            adminUserService.createUser(fullName, email, password, role, phone);
            return "redirect:/admin/users?created=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("me", authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow());
            model.addAttribute("mode", "create");
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("formAction", "/admin/users");
            model.addAttribute("error", e.getMessage());
            model.addAttribute("formFullName", fullName);
            model.addAttribute("formEmail", email);
            model.addAttribute("formPhone", phone);
            model.addAttribute("formRole", role);
            model.addAttribute("formActive", true);
            return "admin-user-form";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Authentication authentication, Model model) {
        AuthUser target = authUsers.findById(id).orElse(null);
        if (target == null) {
            return "redirect:/admin/users?error=User not found";
        }
        model.addAttribute("me", authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow());
        model.addAttribute("mode", "edit");
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("formAction", "/admin/users/" + target.getId());
        model.addAttribute("formFullName", target.getFullName());
        model.addAttribute("formEmail", target.getEmail());
        model.addAttribute("formPhone", target.getPhone());
        model.addAttribute("formRole", target.getRole());
        model.addAttribute("formActive", target.isActive());
        return "admin-user-form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
                              Authentication authentication,
                              @RequestParam String fullName,
                              @RequestParam String email,
                              @RequestParam(required = false) String phone,
                              @RequestParam UserRole role,
                              @RequestParam(required = false) String active,
                              @RequestParam(required = false) String newPassword,
                              Model model) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        boolean isActive = "on".equals(active);

        try {
            adminUserService.updateUser(id, me.getId(), fullName, email, phone, role, isActive, newPassword);
            return "redirect:/admin/users?updated=true";
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            model.addAttribute("me", me);
            model.addAttribute("mode", "edit");
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("formAction", "/admin/users/" + id);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("formFullName", fullName);
            model.addAttribute("formEmail", email);
            model.addAttribute("formPhone", phone);
            model.addAttribute("formRole", role);
            model.addAttribute("formActive", isActive);
            return "admin-user-form";
        }
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            adminUserService.deleteUser(id, me.getId());
            return "redirect:/admin/users?deleted=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/admin/users?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    // Block (suspend/hide) an account with one click - flips is_active to false
    // so the user can no longer log in and, for providers, disappears from the
    // patient-facing directories. Reversible via unblock below.
    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            adminUserService.setActive(id, me.getId(), false);
            return "redirect:/admin/users?blocked=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/admin/users?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            adminUserService.setActive(id, me.getId(), true);
            return "redirect:/admin/users?unblocked=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/admin/users?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/faqs")
    public String listFaqs(Authentication authentication, Model model) {
        model.addAttribute("me", authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow());
        model.addAttribute("faqs", faqService.allFaqs());
        return "admin-faqs";
    }

    @GetMapping("/faqs/new")
    public String newFaqForm(Authentication authentication, Model model) {
        model.addAttribute("me", authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow());
        addFaqForm(model, "Create FAQ", "/admin/faqs", "", "", true, faqService.allFaqs().size() + 1);
        return "admin-faq-form";
    }

    @PostMapping("/faqs")
    public String createFaq(@RequestParam String question, @RequestParam String answer,
                            @RequestParam(required = false) String published, @RequestParam int displayOrder) {
        faqService.create(question, answer, "on".equals(published), displayOrder);
        return "redirect:/admin/faqs?created=true";
    }

    @GetMapping("/faqs/{id}/edit")
    public String editFaqForm(@PathVariable Long id, Authentication authentication, Model model) {
        var faq = faqService.get(id);
        model.addAttribute("me", authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow());
        addFaqForm(model, "Edit FAQ", "/admin/faqs/" + id, faq.getQuestion(), faq.getAnswer(), faq.isPublished(), faq.getDisplayOrder());
        return "admin-faq-form";
    }

    @PostMapping("/faqs/{id}")
    public String updateFaq(@PathVariable Long id, @RequestParam String question, @RequestParam String answer,
                            @RequestParam(required = false) String published, @RequestParam int displayOrder) {
        faqService.update(id, question, answer, "on".equals(published), displayOrder);
        return "redirect:/admin/faqs?updated=true";
    }

    @PostMapping("/faqs/{id}/delete")
    public String deleteFaq(@PathVariable Long id) {
        faqService.delete(id);
        return "redirect:/admin/faqs?deleted=true";
    }

    private void addFaqForm(Model model, String title, String action, String question, String answer,
                            boolean published, int displayOrder) {
        model.addAttribute("title", title);
        model.addAttribute("formAction", action);
        model.addAttribute("formQuestion", question);
        model.addAttribute("formAnswer", answer);
        model.addAttribute("formPublished", published);
        model.addAttribute("formDisplayOrder", displayOrder);
    }
}
