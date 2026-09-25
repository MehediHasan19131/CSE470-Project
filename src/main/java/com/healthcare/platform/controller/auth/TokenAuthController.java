package com.healthcare.platform.controller.auth;
import com.healthcare.platform.model.auth.*;
import com.healthcare.platform.repository.auth.*;
import com.healthcare.platform.service.auth.*;
import com.healthcare.platform.dto.auth.*;
import com.healthcare.platform.security.auth.*;

import com.healthcare.platform.dto.LoginRequest;
import com.healthcare.platform.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Member 1 (Auth & User Management) additions:
 *  - POST /api/auth/register  -> create an account (new; nothing else exposes this yet)
 *  - POST /api/auth/token     -> JWT login for API/mobile clients (new, stateless)
 *
 * The team's existing POST /api/auth/login (session-based, in AuthApiController)
 * is untouched - this controller only adds new routes.
 */
@RestController
@RequestMapping("/api/auth")
public class TokenAuthController {

    private final RegistrationService registrationService;
    private final AuthUserJdbcRepository authUsers;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TokenAuthController(RegistrationService registrationService,
                                AuthUserJdbcRepository authUsers,
                                PasswordEncoder passwordEncoder,
                                JwtService jwtService) {
        this.registrationService = registrationService;
        this.authUsers = authUsers;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthUser user = registrationService.register(request);
            UserResponse response = new UserResponse(
                    user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getPhone(), user.isActive()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorBody(e.getMessage()));
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@Valid @RequestBody LoginRequest request) {
        Optional<AuthUser> userOpt = authUsers.findByEmail(request.email().trim().toLowerCase());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password(), userOpt.get().getPasswordHash())) {
            throw new BadCredentialsException("Incorrect email or password.");
        }

        AuthUser user = userOpt.get();
        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorBody("This account has been deactivated."));
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        TokenResponse response = new TokenResponse(token, "Bearer", user.getRole(), user.getFullName(), jwtService.getExpirationMs());
        return ResponseEntity.ok(response);
    }

    // Small local exception handler so BadCredentialsException returns 401 with a clean body
    // instead of a raw stack trace.
    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorBody> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorBody(e.getMessage()));
    }

    private record ErrorBody(String message) {
    }
}
