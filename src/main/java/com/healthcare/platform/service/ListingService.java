package com.healthcare.platform.service;

import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.dto.ServiceListingResponse;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.List;
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
        return listByRole(UserRole.DOCTOR).stream()
                .filter(item -> contains(item.specialization(), speciality))
                .filter(item -> contains(item.city(), location))
                .toList();
    }

    public List<ServiceListingResponse> hospitals(String location) {
        return listByRole(UserRole.HOSPITAL).stream()
                .filter(item -> contains(item.city(), location))
                .toList();
    }

    public List<ServiceListingResponse> pharmacies(String location, Boolean emergency) {
        return listByRole(UserRole.PHARMACY).stream()
                .filter(item -> contains(item.city(), location))
                .filter(item -> emergency == null || item.emergencyAvailable() == emergency)
                .toList();
    }

    private List<ServiceListingResponse> listByRole(UserRole role) {
        return users.findByRoleAndActiveTrue(role).stream()
                .map(this::toListing)
                .toList();
    }

    private ServiceListingResponse toListing(User user) {
        Profile profile = profiles.findByUserId(user.getId()).orElse(null);
        return ServiceListingResponse.from(user, profile, ratings.averageScore(user.getId()), ratings.countByTargetUserId(user.getId()));
    }

    private boolean contains(String value, String query) {
        return query == null || query.isBlank() || (value != null && value.toLowerCase().contains(query.toLowerCase()));
    }
}
