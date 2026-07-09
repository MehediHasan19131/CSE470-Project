package com.healthcare.platform.dto;

import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;

public record ServiceListingResponse(
        Long id,
        String name,
        String email,
        String phone,
        UserRole role,
        String city,
        String address,
        String specialization,
        String serviceName,
        boolean emergencyAvailable,
        Double latitude,
        Double longitude,
        double averageRating,
        long totalReviews
) {
    public static ServiceListingResponse from(User user, Profile profile, double averageRating, long totalReviews) {
        return new ServiceListingResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                profile == null ? null : profile.getCity(),
                profile == null ? null : profile.getAddress(),
                profile == null ? null : profile.getSpecialization(),
                profile == null ? null : profile.getServiceName(),
                profile != null && profile.isEmergencyAvailable(),
                profile == null ? null : profile.getLatitude(),
                profile == null ? null : profile.getLongitude(),
                Math.round(averageRating * 10.0) / 10.0,
                totalReviews
        );
    }
}
