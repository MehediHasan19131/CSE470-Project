package com.healthcare.platform.config;

import com.healthcare.platform.model.Ambulance;
import com.healthcare.platform.model.AmbulanceRequest;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AmbulanceRepository;
import com.healthcare.platform.repository.AmbulanceRequestRepository;
import com.healthcare.platform.repository.UserRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Ambulance Module (Sprint 2 - Mehedi Hasan).
 *
 * Kept out of HealthcarePlatformApplication's seedData runner on purpose: that
 * method already seeds every other module, and folding this in would have meant
 * hand-resolving ten overlapping conflict regions in one 328-line method. As a
 * separate runner the ambulance data is self-contained and easy to remove.
 *
 * Ordered to run after the main seeder, since it needs the ambulance provider
 * and patient accounts that seedData creates.
 */
@Configuration
public class AmbulanceSeeder {

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    CommandLineRunner seedAmbulances(UserRepository users,
                                     AmbulanceRepository ambulances,
                                     AmbulanceRequestRepository ambulanceRequests) {
        return args -> {
            User provider = users.findByEmail("ambulance@health.test").orElse(null);
            User patient = users.findByEmail("patient@health.test").orElse(null);
            if (provider == null || provider.getRole() != UserRole.AMBULANCE) {
                return;
            }

            Ambulance van1 = createIfMissing(ambulances, provider, "DHAKA-AMB-101", "BASIC", 2,
                    "Rafiq Islam", "+8801800000101", true, 23.7808, 90.4100);
            createIfMissing(ambulances, provider, "DHAKA-AMB-102", "ICU", 2,
                    "Kamal Hossain", "+8801800000102", true, 23.8258, 90.3855);
            createIfMissing(ambulances, provider, "DHAKA-AMB-103", "CARDIAC", 2,
                    "Sultana Begum", "+8801800000103", false, 23.7461, 90.3742);

            if (patient != null && van1 != null && ambulanceRequests.count() == 0) {
                AmbulanceRequest demoRequest = new AmbulanceRequest();
                demoRequest.setPatient(patient);
                demoRequest.setAmbulance(van1);
                demoRequest.setPickupAddress("House 12, Road 5, Dhanmondi, Dhaka");
                demoRequest.setPickupLatitude(23.7461);
                demoRequest.setPickupLongitude(90.3742);
                demoRequest.setDropAddress("Square Hospital, Panthapath, Dhaka");
                demoRequest.setDropLatitude(23.7519);
                demoRequest.setDropLongitude(90.3897);
                demoRequest.setEmergencyType("Accident");
                demoRequest.setNotes("Patient is conscious, minor bleeding.");
                demoRequest.setStatus("COMPLETED");
                demoRequest.setFareEstimate(new BigDecimal("420.00"));
                ambulanceRequests.save(demoRequest);
            }
        };
    }

    private Ambulance createIfMissing(AmbulanceRepository ambulances, User provider, String vehicleNumber,
                                      String vehicleType, int capacity, String driverName, String driverPhone,
                                      boolean available, double latitude, double longitude) {
        return ambulances.findByProviderIdOrderByIdAsc(provider.getId()).stream()
                .filter(a -> a.getVehicleNumber().equals(vehicleNumber))
                .findFirst()
                .orElseGet(() -> ambulances.save(new Ambulance(provider, vehicleNumber, vehicleType, capacity,
                        driverName, driverPhone, available, latitude, longitude)));
    }
}
