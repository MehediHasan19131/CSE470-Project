package com.healthcare.platform.service;

import com.healthcare.platform.model.User;
import com.healthcare.platform.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository users;

    public CurrentUserService(UserRepository users) {
        this.users = users;
    }

    public User get(Authentication authentication) {
        return users.findByEmail(authentication.getName()).orElseThrow();
    }
}
