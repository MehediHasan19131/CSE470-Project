package com.healthcare.platform.admin;

import com.healthcare.platform.auth.AuthUser;
import com.healthcare.platform.auth.AuthUserJdbcRepository;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.review.ReviewService;
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

    public AdminController(AdminUserService adminUserService, AuthUserJdbcRepository authUsers,
                            ReviewService reviewService) {
        this.adminUserService = adminUserService;
        this.authUsers = authUsers;
        this.reviewService = reviewService;
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
}
