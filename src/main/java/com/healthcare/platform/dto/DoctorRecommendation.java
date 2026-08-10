package com.healthcare.platform.dto;

public record DoctorRecommendation(
        Long id,
        String name,
        String specialization,
        String city,
        double averageRating,
        long totalReviews
) {
    public static DoctorRecommendation from(ServiceListingResponse listing) {
        return new DoctorRecommendation(
                listing.id(),
                listing.name(),
                listing.specialization(),
                listing.city(),
                listing.averageRating(),
                listing.totalReviews()
        );
    }
}
