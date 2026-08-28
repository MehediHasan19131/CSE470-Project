package com.healthcare.platform.controller;

import com.healthcare.platform.model.auth.AuthUser;
import com.healthcare.platform.repository.auth.AuthUserJdbcRepository;
import com.healthcare.platform.dto.LoginRequest;
import com.healthcare.platform.dto.LoginResponse;
import com.healthcare.platform.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Session-based login (browser). Uses AuthUserJdbcRepository (plain JDBC) - no ORM.
 * See com.healthcare.platform.controller.auth.TokenAuthController for the separate, stateless
 * JWT login used by API clients.
 */
@RestController
public class AuthApiController {
    private final AuthenticationManager authenticationManager;
    private final AuthUserJdbcRepository authUsers;

    public AuthApiController(AuthenticationManager authenticationManager, AuthUserJdbcRepository authUsers) {
        this.authenticationManager = authenticationManager;
        this.authUsers = authUsers;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        servletRequest.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        AuthUser user = authUsers.findByEmail(request.email().trim().toLowerCase()).orElseThrow();
        return ResponseEntity.ok(new LoginResponse("Login successful", "session", user.getRole(), user.getFullName()));
    }

    @GetMapping("/api/me")
    public UserResponse me(Authentication authentication) {
        // Read through the JDBC repository, matching the session login above.
        // CurrentUserService returns the JPA model.User, which is a different
        // representation of the same `users` row and does not fit UserResponse.
        AuthUser user = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        return UserResponse.from(user);
    }
}
