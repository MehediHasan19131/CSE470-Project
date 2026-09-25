package com.healthcare.platform.dto.review;

import com.healthcare.platform.model.UserRole;

/**
 * A reviewable provider (doctor/hospital/pharmacy/diagnostic centre/ambulance
 * service) plus their current rating, for the "browse providers" directory
 * page ({@code GET /reviews}). Built by a read-only join in
 * {@link ReviewJdbcRepository#findProviders()} against the existing `users`
 * table - doesn't touch or duplicate {@code AuthUser}/{@code AuthUserJdbcRepository}.
 */
public class ProviderSummary {

    private Long id;
    private String fullName;
    private UserRole role;
    private double averageRating;
    private int totalReviews;

    public ProviderSummary() {
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }
}
