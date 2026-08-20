package com.healthcare.platform.service;

import com.healthcare.platform.dto.MapProviderResponse;
import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.ProfileRepository;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Supplies the markers for the Map Integration feature: every active provider
 * (Doctor / Hospital / Pharmacy / Diagnostic / Ambulance) that has a latitude
 * and longitude on its profile. Patients and admins are never plotted.
 */
@Service
public class MapService {

    private static final Set<UserRole> PROVIDER_ROLES = EnumSet.of(
            UserRole.DOCTOR, UserRole.HOSPITAL, UserRole.PHARMACY,
            UserRole.DIAGNOSTIC, UserRole.AMBULANCE);

    private final ProfileRepository profiles;

    public MapService(ProfileRepository profiles) {
        this.profiles = profiles;
    }

    // Read-only transaction so the LAZY Profile -> User association can be read
    // while building the response (rather than relying on open-session-in-view).
    @Transactional(readOnly = true)
    public List<MapProviderResponse> providersWithLocations() {
        List<MapProviderResponse> markers = new ArrayList<>();
        for (Profile profile : profiles.findAll()) {
            if (profile.getLatitude() == null || profile.getLongitude() == null) {
                continue;
            }
            User user = profile.getUser();
            if (user == null || !user.isActive() || !PROVIDER_ROLES.contains(user.getRole())) {
                continue;
            }
            markers.add(new MapProviderResponse(
                    user.getFullName(),
                    user.getRole().name(),
                    profile.getCity(),
                    profile.getAddress(),
                    profile.getSpecialization(),
                    profile.getServiceName(),
                    profile.isEmergencyAvailable(),
                    profile.getLatitude(),
                    profile.getLongitude()));
        }
        return markers;
    }
}
