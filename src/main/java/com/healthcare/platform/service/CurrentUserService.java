package com.healthcare.platform.service;

import com.healthcare.platform.auth.AuthUser;
import com.healthcare.platform.auth.AuthUserJdbcRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Looks up the logged-in user's full record from their Authentication.
 * Uses AuthUserJdbcRepository (plain JDBC) - no ORM.
 */
@Service
public class CurrentUserService {
    private final AuthUserJdbcRepository authUsers;

    public CurrentUserService(AuthUserJdbcRepository authUsers) {
        this.authUsers = authUsers;
    }

    public AuthUser get(Authentication authentication) {
        return authUsers.findByEmail(authentication.getName()).orElseThrow();
    }
}
