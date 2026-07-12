package com.healthcare.platform.auth;

import com.healthcare.platform.model.UserRole;

import java.time.LocalDateTime;

/**
 * Plain Java object for the `users` table, used ONLY by {@link AuthUserJdbcRepository}.
 *
 * This submission has no JPA @Entity classes at all - the team's original shared
 * repo did use one (a User entity via Spring Data JPA), but per the "no ORM" rule
 * this personal submission replaces that entirely with plain JDBC. AuthUser is
 * just a data holder; every query that reads/writes it is written by hand in
 * AuthUserJdbcRepository.
 */
public class AuthUser {

    private Long id;
    private String fullName;
    private String email;
    private String passwordHash;
    private UserRole role;
    private String phone;
    private boolean active;
    private LocalDateTime createdAt;

    public AuthUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
