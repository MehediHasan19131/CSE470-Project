package com.healthcare.platform.controller.api;

import com.healthcare.platform.dto.LoginRequest;
import com.healthcare.platform.dto.LoginResponse;
import com.healthcare.platform.dto.UserResponse;
import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.service.CurrentUserService;
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

@RestController
public class AuthApiController {
    private final CurrentUserService currentUserService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository users;

    public AuthApiController(CurrentUserService currentUserService, AuthenticationManager authenticationManager, UserRepository users) {
        this.currentUserService = currentUserService;
        this.authenticationManager = authenticationManager;
        this.users = users;
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

        User user = users.findByEmail(request.email()).orElseThrow();
        return ResponseEntity.ok(new LoginResponse("Login successful", "session", user.getRole(), user.getFullName()));
    }

    @GetMapping("/api/me")
    public UserResponse me(Authentication authentication) {
        User user = currentUserService.get(authentication);
        return UserResponse.from(user);
    }
}
