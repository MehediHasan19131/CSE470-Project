package com.healthcare.platform.controller.sprint1;

import com.healthcare.platform.dto.ServiceListingResponse;
import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.Rating;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.UserRepository;
import com.healthcare.platform.service.CurrentUserService;
import com.healthcare.platform.service.ListingService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/facilities")
public class FacilityController {

    private final ListingService listingService;
    private final UserRepository users;
    private final ProfileRepository profiles;
    private final RatingRepository ratings;
    private final CurrentUserService currentUserService;

    public FacilityController(ListingService listingService,
                               UserRepository users,
                               ProfileRepository profiles,
                               RatingRepository ratings,
                               CurrentUserService currentUserService) {
        this.listingService = listingService;
        this.users = users;
        this.profiles = profiles;
        this.ratings = ratings;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String dashboard(
            @RequestParam(required = false) String type,
            Authentication authentication,
            Model model) {
        User currentUser = currentUserService.get(authentication);
        List<ServiceListingResponse> facilities = new ArrayList<>();

        if (type == null || type.isBlank() || "HOSPITAL".equalsIgnoreCase(type)) {
            facilities.addAll(listingService.hospitals(null));
        }
        if (type == null || type.isBlank() || "DIAGNOSTIC".equalsIgnoreCase(type)) {
            facilities.addAll(listingService.diagnosticCenters(null));
        }
        if (type == null || type.isBlank() || "PHARMACY".equalsIgnoreCase(type)) {
            facilities.addAll(listingService.pharmacies(null, null));
        }

        facilities.sort(Comparator.comparing(ServiceListingResponse::name, String.CASE_INSENSITIVE_ORDER));

        model.addAttribute("user", currentUser);
        model.addAttribute("facilities", facilities);
        model.addAttribute("selectedType", type != null ? type.toUpperCase() : "ALL");
        return "sprint1/facilities/dashboard";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            Authentication authentication,
            Model model) {
        User currentUser = currentUserService.get(authentication);
        List<ServiceListingResponse> results = new ArrayList<>();

        List<UserRole> targetRoles = new ArrayList<>();
        if (type == null || type.isBlank() || "ALL".equalsIgnoreCase(type)) {
            targetRoles.add(UserRole.HOSPITAL);
            targetRoles.add(UserRole.DIAGNOSTIC);
            targetRoles.add(UserRole.PHARMACY);
        } else {
            if ("HOSPITAL".equalsIgnoreCase(type)) targetRoles.add(UserRole.HOSPITAL);
            if ("DIAGNOSTIC".equalsIgnoreCase(type)) targetRoles.add(UserRole.DIAGNOSTIC);
            if ("PHARMACY".equalsIgnoreCase(type)) targetRoles.add(UserRole.PHARMACY);
        }

        for (UserRole role : targetRoles) {
            List<User> roleUsers = users.findByRole(role);
            for (User u : roleUsers) {
                Profile p = profiles.findByUserId(u.getId()).orElse(null);
                boolean matchesQuery = (query == null || query.isBlank()
                        || u.getFullName().toLowerCase().contains(query.toLowerCase()));
                boolean matchesLocation = (location == null || location.isBlank()
                        || (p != null && p.getCity() != null && p.getCity().toLowerCase().contains(location.toLowerCase())));
                if (matchesQuery && matchesLocation) {
                    List<Rating> ratingList = ratings.findByTargetUserId(u.getId());
                    double avgRating = ratingList.stream().mapToInt(Rating::getScore).average().orElse(0.0);
                    results.add(ServiceListingResponse.from(u, p, avgRating, ratingList.size()));
                }
            }
        }

        results.sort(Comparator.comparing(ServiceListingResponse::name, String.CASE_INSENSITIVE_ORDER));

        model.addAttribute("user", currentUser);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        model.addAttribute("location", location);
        model.addAttribute("selectedType", type != null ? type.toUpperCase() : "ALL");
        return "sprint1/facilities/search";
    }

    @GetMapping("/{type}/{id}")
    public String profile(@PathVariable String type,
                          @PathVariable Long id,
                          Authentication authentication,
                          Model model) {
        User currentUser = currentUserService.get(authentication);
        User facility = users.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found with id: " + id));

        UserRole expectedRole;
        String normalizedType = type.toLowerCase();
        String facilityTypeName;

        switch (normalizedType) {
            case "hospital" -> {
                expectedRole = UserRole.HOSPITAL;
                facilityTypeName = "Hospital";
            }
            case "diagnostic-center", "diagnostic" -> {
                expectedRole = UserRole.DIAGNOSTIC;
                facilityTypeName = "Diagnostic Center";
            }
            case "pharmacy" -> {
                expectedRole = UserRole.PHARMACY;
                facilityTypeName = "Pharmacy";
            }
            default -> throw new RuntimeException("Unknown facility type: " + type);
        }

        if (facility.getRole() != expectedRole) {
            throw new RuntimeException("Facility type mismatch: expected " + expectedRole + " but user has role " + facility.getRole());
        }

        Profile profile = profiles.findByUserId(facility.getId()).orElse(null);
        List<Rating> ratingList = ratings.findByTargetUserId(facility.getId());
        double avgRating = ratingList.stream().mapToInt(Rating::getScore).average().orElse(0.0);

        model.addAttribute("user", currentUser);
        model.addAttribute("facility", facility);
        model.addAttribute("profile", profile);
        model.addAttribute("facilityTypeName", facilityTypeName);
        model.addAttribute("facilityType", expectedRole.name());
        model.addAttribute("ratings", ratingList);
        model.addAttribute("averageRating", Math.round(avgRating * 10.0) / 10.0);
        model.addAttribute("totalReviews", ratingList.size());
        return "sprint1/facilities/profile";
    }
}
