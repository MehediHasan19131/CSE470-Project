package com.healthcare.platform.service;

import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.Rating;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final UserRepository users;
    private final AppointmentRepository appointments;
    private final ProfileRepository profiles;
    private final RatingRepository ratings;
    private final MedicineRepository medicines;

    public DashboardService(
            UserRepository users,
            AppointmentRepository appointments,
            ProfileRepository profiles,
            RatingRepository ratings,
            MedicineRepository medicines
    ) {
        this.users = users;
        this.appointments = appointments;
        this.profiles = profiles;
        this.ratings = ratings;
        this.medicines = medicines;
    }

    public Map<String, Object> dashboard(User currentUser) {
        return switch (currentUser.getRole()) {
            case ADMIN -> adminDashboard(currentUser);
            case PATIENT -> patientDashboard(currentUser);
            case DOCTOR -> doctorDashboard(currentUser);
            case HOSPITAL -> hospitalDashboard(currentUser);
            case PHARMACY -> pharmacyDashboard(currentUser);
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
        data.put("features", List.of("User authentication", "Role management", "Admin settings"));
        return data;
    }

    private Map<String, Object> patientDashboard(User currentUser) {
        Map<String, Object> data = basePayload(currentUser);
        List<Appointment> myAppointments = appointments.findByPatientIdOrderByScheduledAtAsc(currentUser.getId());
        data.put("metrics", Map.of(
                "upcomingAppointments", myAppointments.stream().filter(a -> !"completed".equals(a.getStatus())).count(),
                "completedAppointments", myAppointments.stream().filter(a -> "completed".equals(a.getStatus())).count()
        ));
        data.put("appointments", myAppointments.stream().map(this::appointmentMap).toList());
        data.put("medicalProfile", placeholderMedicalProfile(currentUser));
        data.put("features", List.of("Doctor search", "Hospital listing", "Pharmacy listing", "My appointments", "Medical profile"));
        return data;
    }

    private Map<String, Object> doctorDashboard(User currentUser) {
        Map<String, Object> data = basePayload(currentUser);
        List<Appointment> schedule = appointments.findByDoctorIdOrderByScheduledAtAsc(currentUser.getId());
        List<Appointment> patientSerial = schedule.stream()
                .filter(a -> "pending".equals(a.getStatus()) || "confirmed".equals(a.getStatus()))
                .toList();
        List<Rating> feedback = ratings.findByTargetUserIdOrderByCreatedAtDesc(currentUser.getId());

        data.put("metrics", Map.of(
                "patientsInQueue", patientSerial.size(),
                "todayAppointments", schedule.size(),
                "averageRating", Math.round(ratings.averageScore(currentUser.getId()) * 10.0) / 10.0,
                "totalReviews", ratings.countByTargetUserId(currentUser.getId())
        ));
        data.put("patientSerial", patientSerial.stream().map(this::appointmentMap).toList());
        data.put("schedule", schedule.stream().map(this::appointmentMap).toList());
        data.put("feedback", feedback.stream().map(this::ratingMap).toList());
        data.put("features", List.of("Patient serial", "Feedback", "Schedule"));
        return data;
    }

    private Map<String, Object> hospitalDashboard(User currentUser) {
        Map<String, Object> data = basePayload(currentUser);
        Profile profile = profiles.findByUserId(currentUser.getId()).orElse(null);
        Map<String, Object> availability = placeholderHospitalAvailability();
        data.put("metrics", Map.of(
                "averageRating", Math.round(ratings.averageScore(currentUser.getId()) * 10.0) / 10.0,
                "totalReviews", ratings.countByTargetUserId(currentUser.getId()),
                "emergencyAvailable", profile != null && profile.isEmergencyAvailable(),
                "availableBeds", availability.get("availableBeds"),
                "availableIcu", availability.get("availableIcu"),
                "availableCcu", availability.get("availableCcu"),
                "doctorsAvailable", placeholderDoctorAvailability().stream().filter(d -> (boolean) d.get("available")).count()
        ));
        data.put("availability", availability);
        data.put("doctorAvailability", placeholderDoctorAvailability());
        data.put("services", placeholderHospitalServices());
        data.put("testOffers", placeholderTestOffers());
        data.put("feedback", ratings.findByTargetUserIdOrderByCreatedAtDesc(currentUser.getId()).stream().map(this::ratingMap).toList());
        data.put("features", List.of("Hospital profile", "Bed availability", "Doctor availability", "Service availability", "Test offers", "Patient reviews"));
        return data;
    }

    private Map<String, Object> pharmacyDashboard(User currentUser) {
        Map<String, Object> data = basePayload(currentUser);
        List<Medicine> stock = medicines.findByActiveTrueOrderByNameAsc();
        List<Map<String, Object>> discounts = placeholderPharmacyDiscounts();
        data.put("metrics", Map.of(
                "medicinesInStock", stock.size(),
                "lowStockItems", stock.stream().filter(m -> m.getStockQuantity() <= 10).count(),
                "totalUnits", stock.stream().mapToInt(Medicine::getStockQuantity).sum(),
                "activeDiscounts", discounts.size()
        ));
        data.put("medicines", stock.stream().map(this::medicineMap).toList());
        data.put("discountAds", discounts);
        data.put("features", List.of("Sell medicine", "Stock management", "Discount advertisements"));
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
        data.put("profile", profileMap(profiles.findByUserId(currentUser.getId()).orElse(null)));
        return data;
    }

    private Map<String, Object> appointmentMap(Appointment appointment) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", appointment.getId());
        map.put("patientName", appointment.getPatient().getFullName());
        map.put("doctorName", appointment.getDoctor().getFullName());
        map.put("scheduledAt", appointment.getScheduledAt() == null ? "" : appointment.getScheduledAt().toString());
        map.put("status", appointment.getStatus());
        map.put("reason", appointment.getReason() == null ? "" : appointment.getReason());
        return map;
    }

    private Map<String, Object> ratingMap(Rating rating) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("score", rating.getScore());
        map.put("comment", rating.getComment() == null ? "" : rating.getComment());
        map.put("reviewerName", rating.getReviewerUser() == null ? "Anonymous" : rating.getReviewerUser().getFullName());
        map.put("createdAt", rating.getCreatedAt() == null ? "" : rating.getCreatedAt().toString());
        return map;
    }

    private Map<String, Object> medicineMap(Medicine medicine) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", medicine.getId());
        map.put("name", medicine.getName());
        map.put("description", medicine.getDescription() == null ? "" : medicine.getDescription());
        map.put("price", medicine.getPrice());
        map.put("stockQuantity", medicine.getStockQuantity());
        return map;
    }

    private Map<String, Object> profileMap(Profile profile) {
        if (profile == null) {
            return Map.of();
        }
        return Map.of(
                "city", value(profile.getCity()),
                "address", value(profile.getAddress()),
                "specialization", value(profile.getSpecialization()),
                "serviceName", value(profile.getServiceName()),
                "emergencyAvailable", profile.isEmergencyAvailable(),
                "latitude", profile.getLatitude() == null ? "" : profile.getLatitude(),
                "longitude", profile.getLongitude() == null ? "" : profile.getLongitude()
        );
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    // Placeholder data for Sprint 4 backend integration
    private Map<String, Object> placeholderHospitalAvailability() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("totalBeds", 120);
        map.put("availableBeds", 34);
        map.put("totalIcu", 18);
        map.put("availableIcu", 5);
        map.put("totalCcu", 12);
        map.put("availableCcu", 3);
        return map;
    }

    private List<Map<String, Object>> placeholderDoctorAvailability() {
        return List.of(
                doctorAvailabilityMap("Dr. Ayesha Rahman", "Cardiology", true),
                doctorAvailabilityMap("Dr. Karim Hossain", "Neurology", true),
                doctorAvailabilityMap("Dr. Nusrat Jahan", "Pediatrics", false),
                doctorAvailabilityMap("Dr. Rafiq Ahmed", "Orthopedics", true),
                doctorAvailabilityMap("Dr. Sabina Khatun", "Gynecology", false)
        );
    }

    private Map<String, Object> doctorAvailabilityMap(String name, String specialization, boolean available) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("doctorName", name);
        map.put("specialization", specialization);
        map.put("available", available);
        return map;
    }

    private List<Map<String, Object>> placeholderHospitalServices() {
        return List.of(
                serviceMap("Emergency Care", true),
                serviceMap("General Surgery", true),
                serviceMap("Radiology & Imaging", true),
                serviceMap("Dialysis Unit", false),
                serviceMap("Blood Bank", true),
                serviceMap("Ambulance Service", true)
        );
    }

    private Map<String, Object> serviceMap(String name, boolean available) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("serviceName", name);
        map.put("available", available);
        return map;
    }

    private List<Map<String, Object>> placeholderTestOffers() {
        return List.of(
                testOfferMap("Complete Blood Count (CBC)", 450, 315, 30),
                testOfferMap("Lipid Profile", 1200, 840, 30),
                testOfferMap("Thyroid Panel (T3, T4, TSH)", 1800, 1260, 30),
                testOfferMap("HbA1c (Diabetes)", 950, 665, 30),
                testOfferMap("Chest X-Ray", 600, 420, 30)
        );
    }

    private Map<String, Object> testOfferMap(String testName, int regularPrice, int offerPrice, int discountPercent) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("testName", testName);
        map.put("regularPrice", regularPrice);
        map.put("offerPrice", offerPrice);
        map.put("discountPercent", discountPercent);
        return map;
    }

    private List<Map<String, Object>> placeholderPharmacyDiscounts() {
        return List.of(
                discountAdMap("Paracetamol 500mg", "Pain Relief", 25, "Buy 2 Get 1 Free", "৳12", "৳9"),
                discountAdMap("Amoxicillin 250mg", "Antibiotic", 20, "20% Off This Week", "৳85", "৳68"),
                discountAdMap("Vitamin D3 60K", "Supplements", 15, "15% Discount", "৳320", "৳272"),
                discountAdMap("Cetirizine 10mg", "Allergy", 30, "Flash Sale — 30% Off", "৳45", "৳32")
        );
    }

    private Map<String, Object> discountAdMap(String product, String category, int discountPercent, String tagline, String regularPrice, String offerPrice) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("product", product);
        map.put("category", category);
        map.put("discountPercent", discountPercent);
        map.put("tagline", tagline);
        map.put("regularPrice", regularPrice);
        map.put("offerPrice", offerPrice);
        return map;
    }

    private Map<String, Object> placeholderMedicalProfile(User currentUser) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("bloodGroup", "B+");
        profile.put("height", "172 cm");
        profile.put("weight", "68 kg");
        profile.put("allergies", List.of("Penicillin", "Dust mites"));
        profile.put("chronicConditions", List.of("Mild hypertension"));
        profile.put("vitals", List.of(
                vitalMap("Blood Pressure", "128/82 mmHg", "2026-07-01"),
                vitalMap("Heart Rate", "72 bpm", "2026-07-01"),
                vitalMap("Blood Sugar (Fasting)", "98 mg/dL", "2026-06-15"),
                vitalMap("SpO2", "98%", "2026-07-01")
        ));
        profile.put("prescriptions", List.of(
                prescriptionMap("Amlodipine 5mg", "Dr. Karim Hossain", "Once daily", "2026-05-10"),
                prescriptionMap("Vitamin D3 60K", "Dr. Ayesha Rahman", "Once weekly", "2026-06-02")
        ));
        profile.put("labResults", List.of(
                labResultMap("Lipid Profile", "Normal", "2026-06-15"),
                labResultMap("Complete Blood Count", "Normal", "2026-06-15"),
                labResultMap("HbA1c", "5.4%", "2026-05-20")
        ));
        profile.put("visitHistory", List.of(
                visitMap("General Checkup", "Dr. Karim Hossain", "City General Hospital", "2026-07-01"),
                visitMap("Cardiology Follow-up", "Dr. Ayesha Rahman", "City General Hospital", "2026-06-02"),
                visitMap("Blood Test", "Lab Services", "City General Hospital", "2026-05-20")
        ));
        profile.put("patientName", currentUser.getFullName());
        return profile;
    }

    private Map<String, Object> vitalMap(String name, String value, String date) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("value", value);
        map.put("date", date);
        return map;
    }

    private Map<String, Object> prescriptionMap(String medicine, String doctor, String dosage, String date) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("medicine", medicine);
        map.put("doctor", doctor);
        map.put("dosage", dosage);
        map.put("date", date);
        return map;
    }

    private Map<String, Object> labResultMap(String test, String result, String date) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("test", test);
        map.put("result", result);
        map.put("date", date);
        return map;
    }

    private Map<String, Object> visitMap(String reason, String provider, String facility, String date) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("reason", reason);
        map.put("provider", provider);
        map.put("facility", facility);
        map.put("date", date);
        return map;
    }
}
