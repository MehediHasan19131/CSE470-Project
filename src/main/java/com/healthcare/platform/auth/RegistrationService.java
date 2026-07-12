package com.healthcare.platform.auth;

import com.healthcare.platform.model.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final AuthUserJdbcRepository authUsers;
    private final PasswordEncoder passwordEncoder;

    // PasswordEncoder here is the SAME bean SecurityConfig already defines (BCryptPasswordEncoder).
    // Reusing it guarantees a password hashed here can be verified by the existing
    // session-based login (DaoAuthenticationProvider) without any changes there.
    public RegistrationService(AuthUserJdbcRepository authUsers, PasswordEncoder passwordEncoder) {
        this.authUsers = authUsers;
        this.passwordEncoder = passwordEncoder;
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

        return authUsers.insert(user);
    }
}
