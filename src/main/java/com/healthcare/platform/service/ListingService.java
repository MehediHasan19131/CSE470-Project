package com.healthcare.platform.service;

import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.Rating;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.dto.ServiceListingResponse;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ListingService {
    private final UserRepository users;
    private final ProfileRepository profiles;
    private final RatingRepository ratings;

    public ListingService(UserRepository users, ProfileRepository profiles, RatingRepository ratings) {
        this.users = users;
        this.profiles = profiles;
        this.ratings = ratings;
    }

    public List<ServiceListingResponse> doctors(String speciality, String location) {
        return users.findByRole(UserRole.DOCTOR).stream()
                .filter(u -> speciality == null || speciality.isBlank()
                        || (u.getProfile() != null && speciality.equalsIgnoreCase(u.getProfile().getSpecialization())))
                .filter(u -> location == null || location.isBlank()
                        || (u.getProfile() != null && location.equalsIgnoreCase(u.getProfile().getCity())))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ServiceListingResponse> hospitals(String location) {
        return users.findByRole(UserRole.HOSPITAL).stream()
                .filter(u -> location == null || location.isBlank()
                        || (u.getProfile() != null && location.equalsIgnoreCase(u.getProfile().getCity())))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ServiceListingResponse> pharmacies(String location, Boolean emergency) {
        return users.findByRole(UserRole.PHARMACY).stream()
                .filter(u -> location == null || location.isBlank()
                        || (u.getProfile() != null && location.equalsIgnoreCase(u.getProfile().getCity())))
                .filter(u -> emergency == null || (u.getProfile() != null && u.getProfile().isEmergencyAvailable() == emergency))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ServiceListingResponse> diagnosticCenters(String location) {
        return users.findByRole(UserRole.DIAGNOSTIC).stream()
                .filter(u -> location == null || location.isBlank()
                        || (u.getProfile() != null && location.equalsIgnoreCase(u.getProfile().getCity())))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ServiceListingResponse toResponse(User user) {
        Profile profile = profiles.findByUserId(user.getId()).orElse(null);
        List<Rating> ratingList = ratings.findByTargetUserId(user.getId());
        double avgRating = ratingList.stream().mapToInt(Rating::getScore).average().orElse(0.0);
        return ServiceListingResponse.from(user, profile, avgRating, ratingList.size());
    }
}
