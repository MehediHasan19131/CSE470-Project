package com.healthcare.platform.service;

import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
<<<<<<< HEAD
import com.healthcare.platform.repository.*;
import java.util.*;
=======
import com.healthcare.platform.repository.AppSettingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
>>>>>>> origin/sprint1-Mehedi
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final UserRepository users;
<<<<<<< HEAD
    private final AppointmentRepository appointments;
    private final MedicineRepository medicines;
    private final RatingRepository ratings;

    public DashboardService(UserRepository users, AppointmentRepository appointments,
                            MedicineRepository medicines, RatingRepository ratings) {
        this.users = users;
        this.appointments = appointments;
        this.medicines = medicines;
        this.ratings = ratings;
    }

    public Map<String, Object> dashboard(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalPatients", users.findByRole(UserRole.PATIENT).size());
        data.put("totalDoctors", users.findByRole(UserRole.DOCTOR).size());
        data.put("totalHospitals", users.findByRole(UserRole.HOSPITAL).size());
        data.put("totalPharmacies", users.findByRole(UserRole.PHARMACY).size());
        data.put("totalAppointments", appointments.count());
        data.put("totalMedicines", medicines.count());
=======
    private final AppSettingRepository settings;

    public DashboardService(UserRepository users, AppSettingRepository settings) {
        this.users = users;
        this.settings = settings;
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
        data.put("metrics", Map.of(
                "totalUsers", users.count(),
                "activeUsers", users.findAll().stream().filter(User::isActive).count(),
                "adminUsers", users.countByRole(UserRole.ADMIN),
                "patientUsers", users.countByRole(UserRole.PATIENT)
        ));
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
>>>>>>> origin/sprint1-Mehedi
        return data;
    }
}
