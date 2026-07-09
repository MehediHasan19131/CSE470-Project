package com.healthcare.platform;

import com.healthcare.platform.model.AppSetting;
import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.Rating;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AppSettingRepository;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HealthcarePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcarePlatformApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            UserRepository users,
            ProfileRepository profiles,
            RatingRepository ratings,
            AppSettingRepository settings,
            AppointmentRepository appointments,
            MedicineRepository medicines,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User admin = createUser(users, passwordEncoder, "Admin User", "admin@health.test", UserRole.ADMIN, "+8801700000000");
            User patient = createUser(users, passwordEncoder, "Nadia Rahman", "patient@health.test", UserRole.PATIENT, "+8801700000001");
            User doctor = createUser(users, passwordEncoder, "Dr. Arif Khan", "doctor@health.test", UserRole.DOCTOR, "+8801700000002");
            User hospital = createUser(users, passwordEncoder, "City Care Hospital", "hospital@health.test", UserRole.HOSPITAL, "+8801700000003");
            User pharmacy = createUser(users, passwordEncoder, "MediQuick Pharmacy", "pharmacy@health.test", UserRole.PHARMACY, "+8801700000004");
            User diagnostic = createUser(users, passwordEncoder, "Prime Diagnostic Centre", "diagnostic@health.test", UserRole.DIAGNOSTIC, "+8801700000005");
            User ambulance = createUser(users, passwordEncoder, "Rapid Ambulance", "ambulance@health.test", UserRole.AMBULANCE, "+8801700000006");

            createProfileIfMissing(profiles, admin, "Road 1, Dhaka", "Dhaka", "Platform admin profile.", null, null, "Platform Admin", false, 23.8103, 90.4125);
            createProfileIfMissing(profiles, patient, "Road 2, Dhaka", "Dhaka", "Patient profile.", null, null, "Patient", false, 23.8203, 90.4225);
            createProfileIfMissing(profiles, doctor, "Road 3, Dhaka", "Dhaka", "Cardiology specialist profile.", "Cardiology", "DOC-1001", null, false, 23.8303, 90.4325);
            createProfileIfMissing(profiles, hospital, "Road 4, Dhaka", "Dhaka", "Multi-speciality hospital.", null, "HOSP-1001", "Multi-speciality Hospital", true, 23.8403, 90.4425);
            createProfileIfMissing(profiles, pharmacy, "Road 5, Dhaka", "Dhaka", "24/7 pharmacy service.", null, "PHAR-1001", "24/7 Pharmacy", true, 23.8503, 90.4525);
            createProfileIfMissing(profiles, diagnostic, "Road 6, Dhaka", "Dhaka", "Diagnostic centre service.", null, "DIAG-1001", "Diagnostics", false, 23.8603, 90.4625);
            createProfileIfMissing(profiles, ambulance, "Road 7, Dhaka", "Dhaka", "Emergency ambulance service.", null, "AMB-1001", "Emergency Ambulance", true, 23.8703, 90.4725);

            if (ratings.count() == 0) {
                ratings.save(new Rating(doctor, patient, 5, "Good service"));
                ratings.save(new Rating(hospital, patient, 5, "Good service"));
                ratings.save(new Rating(pharmacy, patient, 5, "Good service"));
                ratings.save(new Rating(ambulance, patient, 5, "Good service"));
            }

            if (appointments.count() == 0) {
                appointments.save(createAppointment(patient, doctor, LocalDateTime.now().plusHours(1), "pending", "Chest pain follow-up"));
                appointments.save(createAppointment(patient, doctor, LocalDateTime.now().plusHours(3), "confirmed", "Routine checkup"));
                appointments.save(createAppointment(patient, doctor, LocalDateTime.now().plusDays(1), "pending", "Blood pressure review"));
            }

            if (medicines.count() == 0) {
                medicines.save(new Medicine("Paracetamol 500mg", "Pain relief and fever reducer", new BigDecimal("2.50"), 120));
                medicines.save(new Medicine("Amoxicillin 250mg", "Antibiotic capsules", new BigDecimal("8.00"), 45));
                medicines.save(new Medicine("Vitamin C 1000mg", "Immune support tablets", new BigDecimal("5.50"), 8));
                medicines.save(new Medicine("Cough Syrup", "Dry cough relief", new BigDecimal("6.75"), 30));
            }

            createSettingIfMissing(settings, "appointment_reminders", "enabled");
            createSettingIfMissing(settings, "ai_agent_status", "planned");
            createSettingIfMissing(settings, "payment_gateway", "sandbox");
        };
    }

    private User createUser(UserRepository users, PasswordEncoder passwordEncoder, String name, String email, UserRole role, String phone) {
        User user = users.findByEmail(email).orElseGet(() -> new User(name, email, "", role, phone, true));
        user.setFullName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRole(role);
        user.setPhone(phone);
        user.setActive(true);
        return users.save(user);
    }

    private void createProfileIfMissing(
            ProfileRepository profiles,
            User user,
            String address,
            String city,
            String bio,
            String specialization,
            String licenseNumber,
            String serviceName,
            boolean emergencyAvailable,
            Double latitude,
            Double longitude
    ) {
        if (profiles.findByUserId(user.getId()).isEmpty()) {
            profiles.save(new Profile(user, address, city, bio, specialization, licenseNumber, serviceName, emergencyAvailable, latitude, longitude));
        }
    }

    private void createSettingIfMissing(AppSettingRepository settings, String key, String value) {
        if (settings.findByKey(key).isEmpty()) {
            settings.save(new AppSetting(key, value));
        }
    }

    private Appointment createAppointment(User patient, User doctor, LocalDateTime scheduledAt, String status, String reason) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setScheduledAt(scheduledAt);
        appointment.setStatus(status);
        appointment.setReason(reason);
        return appointment;
    }
}
