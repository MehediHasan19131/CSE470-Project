package com.healthcare.platform.service.admin;

import com.healthcare.platform.model.auth.AuthUser;
import com.healthcare.platform.repository.auth.AuthUserJdbcRepository;
import com.healthcare.platform.service.blog.BlogService;
import com.healthcare.platform.service.healthprofile.HealthProfileService;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.review.ReviewJdbcRepository;
import com.healthcare.platform.service.MedicineReminderService;
import com.healthcare.platform.service.RecordAccessService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business rules behind the Admin user-management panel: create, edit, and
 * delete ANY account, of any role (including other admins). Reachable only
 * by ADMIN - enforced in SecurityConfig, not here.
 * <p>
 * Deliberately separate from {@code RegistrationService} (the public
 * self-signup flow) rather than reusing it: self-signup intentionally blocks
 * creating ADMIN accounts, but an admin creating another admin is exactly
 * what this panel needs to allow.
 */
@Service
public class AdminUserService {

    private final AuthUserJdbcRepository authUsers;
    private final ReviewJdbcRepository reviews;
    private final HealthProfileService healthProfile;
    private final BlogService blog;
    private final RecordAccessService recordAccess;
    private final MedicineReminderService medicineReminders;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AuthUserJdbcRepository authUsers, ReviewJdbcRepository reviews,
                             HealthProfileService healthProfile, BlogService blog,
                             RecordAccessService recordAccess, MedicineReminderService medicineReminders,
                             PasswordEncoder passwordEncoder) {
        this.authUsers = authUsers;
        this.reviews = reviews;
        this.healthProfile = healthProfile;
        this.blog = blog;
        this.recordAccess = recordAccess;
        this.medicineReminders = medicineReminders;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AuthUser> listUsers() {
        return authUsers.findAll();
    }

    /** Accounts still waiting on admin approval (Doctor/Hospital/Pharmacy/Diagnostic/Ambulance sign-ups). */
    public List<AuthUser> listPendingApproval() {
        return authUsers.findAll().stream()
                .filter(u -> !u.isActive() && u.getRole() != UserRole.PATIENT && u.getRole() != UserRole.ADMIN)
                .toList();
    }

    /** One-click approve from the Admin dashboard/user list - just flips the account active, nothing else changes. */
    public void approveUser(Long id) {
        AuthUser target = authUsers.findById(id).orElseThrow(() -> new NoSuchElementException("User not found."));
        authUsers.updateAsAdmin(target.getId(), target.getFullName(), target.getEmail(), target.getPhone(), target.getRole(), true);
    }

    public AuthUser createUser(String fullName, String email, String password, UserRole role, String phone) {
        String normalizedEmail = email.trim().toLowerCase();

        if (authUsers.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }

        AuthUser user = new AuthUser();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setPhone(phone);
        // An admin creating an account directly is itself the approval - no
        // pending state needed, unlike public self-registration below.
        user.setActive(true);

        return authUsers.insert(user);
    }

    /** newPasswordOrNull: leave blank/null on the edit form to keep the existing password unchanged. */
    public AuthUser updateUser(Long id, Long requesterId, String fullName, String email, String phone,
                                UserRole role, boolean active, String newPasswordOrNull) {
        AuthUser existing = authUsers.findById(id).orElseThrow(() -> new NoSuchElementException("User not found."));

        if (id.equals(requesterId) && (role != UserRole.ADMIN || !active)) {
            throw new IllegalStateException("You can't remove your own admin access or deactivate your own account.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // An admin's live session is tied to the email they logged in with (see
        // CurrentUserService). Changing it here would break that session until they
        // log back in - same reason /profile itself won't let anyone edit their own
        // email either. Editing someone else's email is fine.
        if (id.equals(requesterId) && !normalizedEmail.equals(existing.getEmail())) {
            throw new IllegalStateException("You can't change your own email address here - use My Profile instead.");
        }

        authUsers.findByEmail(normalizedEmail).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new IllegalArgumentException("Another account already uses that email.");
            }
        });

        authUsers.updateAsAdmin(id, fullName.trim(), normalizedEmail, phone, role, active);

        if (newPasswordOrNull != null && !newPasswordOrNull.isBlank()) {
            if (newPasswordOrNull.length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters.");
            }
            authUsers.updatePasswordHash(id, passwordEncoder.encode(newPasswordOrNull));
        }

        return authUsers.findById(id).orElseThrow();
    }

    public void deleteUser(Long id, Long requesterId) {
        if (id.equals(requesterId)) {
            throw new IllegalStateException("You can't delete your own account while logged in.");
        }

        AuthUser target = authUsers.findById(id).orElseThrow(() -> new NoSuchElementException("User not found."));

        if (target.getRole() == UserRole.ADMIN && authUsers.countByRole(UserRole.ADMIN) <= 1) {
            throw new IllegalStateException("Can't delete the last remaining admin account.");
        }

        // Clean up review history first - `reviews`/`rating_summaries` both have a
        // foreign key on users.id, so the user row can't be deleted while either
        // still references it.
        List<Long> targetsTheyReviewed = reviews.findDistinctTargetsReviewedBy(id);
        reviews.deleteReviewsInvolvingUser(id);
        for (Long reviewedTargetId : targetsTheyReviewed) {
            if (!reviewedTargetId.equals(id)) {
                reviews.refreshRatingSummary(reviewedTargetId);
            }
        }
        reviews.deleteRatingSummary(id);

        // Same reason as above: `medical_history`/`allergies` also have a foreign
        // key on users.id (Sprint 3 - Health Profile). A provider account never
        // has any of these rows, but it's harmless (and simpler) to always call
        // this rather than branch on role.
        healthProfile.deleteAllForPatient(id);

        // Same reason again: `posts`/`comments` have a foreign key on users.id
        // (Sprint 4 - Health Blog & Community). Deletes their own posts (and
        // every comment on those posts, including other people's replies) plus
        // any comments they left on other people's posts.
        blog.deleteAllForAuthor(id);

        // Same reason again: `record_access_grants` has a foreign key on users.id
        // for BOTH patient_id and doctor_id (Medical Records Sharing), and
        // `medicine_reminders` has one for patient_id.
        recordAccess.deleteAllForUser(id);
        medicineReminders.deleteAllForPatient(id);

        authUsers.deleteById(id);
    }

    /**
     * One-click block (suspend/hide) or unblock, without opening the full edit
     * form. Blocking flips is_active to false: SecurityConfig then refuses that
     * account's login (.disabled(!active)) and inactive providers drop out of
     * the patient-facing directories. Unblocking flips it back to true.
     */
    public void setActive(Long id, Long requesterId, boolean active) {
        if (id.equals(requesterId) && !active) {
            throw new IllegalStateException("You can't block your own account while logged in.");
        }
        AuthUser target = authUsers.findById(id).orElseThrow(() -> new NoSuchElementException("User not found."));
        if (!active && target.getRole() == UserRole.ADMIN && authUsers.countByRole(UserRole.ADMIN) <= 1) {
            throw new IllegalStateException("Can't block the last remaining admin account.");
        }
        authUsers.updateAsAdmin(target.getId(), target.getFullName(), target.getEmail(),
                target.getPhone(), target.getRole(), active);
    }
}
