package com.healthcare.platform.auth;

import com.healthcare.platform.model.UserRole;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    // Provider/facility roles are gate-kept by an admin before they can log in
    // and appear to patients (e.g. in the Hospital & Diagnostic directory, the
    // doctor search, the pharmacy list) - anyone could otherwise claim to run a
    // hospital or pharmacy just by signing up. Patients don't need vetting, so
    // they can log in immediately.
    private static final Set<UserRole> ROLES_REQUIRING_APPROVAL = EnumSet.of(
            UserRole.DOCTOR, UserRole.HOSPITAL, UserRole.PHARMACY,
            UserRole.DIAGNOSTIC, UserRole.AMBULANCE
    );

    private final AuthUserJdbcRepository authUsers;
    private final PasswordEncoder passwordEncoder;

    // PasswordEncoder here is the SAME bean SecurityConfig already defines (BCryptPasswordEncoder).
    // Reusing it guarantees a password hashed here can be verified by the existing
    // session-based login (DaoAuthenticationProvider) without any changes there.
    public RegistrationService(AuthUserJdbcRepository authUsers, PasswordEncoder passwordEncoder) {
        this.authUsers = authUsers;
        this.passwordEncoder = passwordEncoder;
    }

    public static boolean requiresApproval(UserRole role) {
        return ROLES_REQUIRING_APPROVAL.contains(role);
    }

    public AuthUser register(RegisterRequest request) {
        if (request.getRole() == UserRole.ADMIN) {
            throw new IllegalArgumentException("Admin accounts can't be created through registration.");
        }

        String email = request.getEmail().trim().toLowerCase();

        if (authUsers.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        AuthUser user = new AuthUser();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        // Patients can log in right away; Doctor/Hospital/Pharmacy/Diagnostic/
        // Ambulance accounts stay disabled until an admin approves them from
        // the Admin > Manage Users panel (same "active" flag admin toggles there).
        user.setActive(!requiresApproval(request.getRole()));

        return authUsers.insert(user);
    }
}
