package com.healthcare.platform.service;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AppSettingRepository;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final UserRepository users;
    private final AppSettingRepository settings;
    private final AppointmentRepository appointments;
    private final MedicineRepository medicines;
    private final RatingRepository ratings;

    public DashboardService(UserRepository users,
                            AppSettingRepository settings,
                            AppointmentRepository appointments,
                            MedicineRepository medicines,
                            RatingRepository ratings) {
        this.users = users;
        this.settings = settings;
        this.appointments = appointments;
        this.medicines = medicines;
        this.ratings = ratings;
    }

    public Map<String, Object> dashboard(User currentUser) {
        return switch (currentUser.getRole()) {
            case ADMIN -> adminDashboard(currentUser);
            case PATIENT -> roleDashboard(currentUser, List.of(
                    "Appointments — Member 2 (Doctor & Patient Module)",
                    "Health profile — Member 2 (Doctor & Patient Module)",
                    "Doctor search — Member 2 (Doctor & Patient Module)"
            ));
            case DOCTOR -> roleDashboard(currentUser, List.of(
                    "Patient serial — Member 2 (Doctor & Patient Module)",
                    "Schedule — Member 2 (Doctor & Patient Module)",
                    "Feedback — Member 2 (Doctor & Patient Module)"
            ));
            case HOSPITAL -> roleDashboard(currentUser, List.of(
                    "Bed availability — Member 3 (Hospital Module)",
                    "Doctor availability — Member 3 (Hospital Module)",
                    "Service availability — Member 3 (Hospital Module)",
                    "Test offers — Member 3 (Diagnostic Center Module)"
            ));
            case PHARMACY -> roleDashboard(currentUser, List.of(
                    "Stock management — Member 3 (Pharmacy Module)",
                    "Sell medicine — Member 3 (Pharmacy Module)",
                    "Discount advertisements — Member 3 (Pharmacy Module)"
            ));
            case DIAGNOSTIC, AMBULANCE -> unavailableDashboard(currentUser);
        };
    }

    private Map<String, Object> adminDashboard(User currentUser) {
        Map<String, Object> data = basePayload(currentUser);

        long hospitals = users.countByRole(UserRole.HOSPITAL);
        long pharmacies = users.countByRole(UserRole.PHARMACY);

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("totalUsers", users.count());
        metrics.put("activeUsers", users.findAll().stream().filter(User::isActive).count());
        metrics.put("adminUsers", users.countByRole(UserRole.ADMIN));
        metrics.put("patientUsers", users.countByRole(UserRole.PATIENT));
        metrics.put("doctorUsers", users.countByRole(UserRole.DOCTOR));
        metrics.put("hospitalUsers", hospitals);
        metrics.put("pharmacyUsers", pharmacies);
        metrics.put("totalFacilities", hospitals + pharmacies);
        metrics.put("totalAppointments", appointments.count());
        metrics.put("totalMedicines", medicines.count());
        metrics.put("totalRatings", ratings.count());
        data.put("metrics", metrics);

        data.put("settings", settings.findAll().stream()
                .map(setting -> Map.of("key", setting.getKey(), "value", setting.getValue()))
                .toList());
        data.put("features", List.of("Admin settings", "Dashboard APIs", "User role management"));
        return data;
    }

    private Map<String, Object> roleDashboard(User currentUser, List<String> modules) {
        Map<String, Object> data = basePayload(currentUser);
        data.put("modules", modules);
        data.put("integrationNote", "Dashboard shell by Member 4. Module data is owned by other sprint members.");
        return data;
    }

    private Map<String, Object> unavailableDashboard(User currentUser) {
        Map<String, Object> data = basePayload(currentUser);
        data.put("message", "This role dashboard is planned for a later sprint.");
        return data;
    }

    private Map<String, Object> basePayload(User currentUser) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("role", currentUser.getRole());
        data.put("user", Map.of(
                "id", currentUser.getId(),
                "name", currentUser.getFullName(),
                "email", currentUser.getEmail()
        ));
        return data;
    }
}
