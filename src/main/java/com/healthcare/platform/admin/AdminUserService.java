package com.healthcare.platform.admin;

import com.healthcare.platform.auth.AuthUser;
import com.healthcare.platform.auth.AuthUserJdbcRepository;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.review.ReviewJdbcRepository;
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
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AuthUserJdbcRepository authUsers, ReviewJdbcRepository reviews, PasswordEncoder passwordEncoder) {
        this.authUsers = authUsers;
        this.reviews = reviews;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AuthUser> listUsers() {
        return authUsers.findAll();
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

        // Clean up review history first - `reviews`/`ratings` both have a foreign
        // key on users.id, so the user row can't be deleted while either still
        // references it.
        List<Long> targetsTheyReviewed = reviews.findDistinctTargetsReviewedBy(id);
        reviews.deleteReviewsInvolvingUser(id);
        for (Long reviewedTargetId : targetsTheyReviewed) {
            if (!reviewedTargetId.equals(id)) {
                reviews.refreshRatingSummary(reviewedTargetId);
            }
        }
        reviews.deleteRatingSummary(id);

        authUsers.deleteById(id);
    }
}
