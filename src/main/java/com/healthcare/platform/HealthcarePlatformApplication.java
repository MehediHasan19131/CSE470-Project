package com.healthcare.platform;

import com.healthcare.platform.model.AppSetting;
import com.healthcare.platform.model.Appointment;
import com.healthcare.platform.model.BedAvailability;
import com.healthcare.platform.model.Campaign;
import com.healthcare.platform.model.Donor;
import com.healthcare.platform.model.Faq;
import com.healthcare.platform.model.HospitalDoctorAvailability;
import com.healthcare.platform.model.HospitalServiceOffering;
import com.healthcare.platform.model.Medicine;
import com.healthcare.platform.model.Profile;
import com.healthcare.platform.model.Rating;
import com.healthcare.platform.model.TestOffer;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.AppSettingRepository;
import com.healthcare.platform.repository.AppointmentRepository;
import com.healthcare.platform.repository.BedAvailabilityRepository;
import com.healthcare.platform.repository.CampaignRepository;
import com.healthcare.platform.repository.DonationRepository;
import com.healthcare.platform.repository.DonorRepository;
import com.healthcare.platform.repository.FaqRepository;
import com.healthcare.platform.repository.HospitalDoctorAvailabilityRepository;
import com.healthcare.platform.repository.HospitalServiceOfferingRepository;
import com.healthcare.platform.repository.MedicineRepository;
import com.healthcare.platform.repository.ProfileRepository;
import com.healthcare.platform.repository.RatingRepository;
import com.healthcare.platform.repository.TestOfferRepository;
import com.healthcare.platform.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class HealthcarePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthcarePlatformApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(
            UserRepository users,
            ProfileRepository profiles,
            RatingRepository ratings,
            AppSettingRepository settings,
            AppointmentRepository appointments,
            MedicineRepository medicines,
            CampaignRepository campaigns,
            DonationRepository donationsRepo,
            DonorRepository donors,
            FaqRepository faqs,
            BedAvailabilityRepository beds,
            HospitalDoctorAvailabilityRepository doctorAvailabilities,
            HospitalServiceOfferingRepository hospitalServices,
            TestOfferRepository testOffers,
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

            // Doctor & Patient Module (Sprint 1 - Imtiaz Zaman Sami)
            User doctor2 = createUser(users, passwordEncoder, "Dr. Ayesha Rahman", "ayesha.rahman@health.test", UserRole.DOCTOR, "+8801700000010");
            User doctor3 = createUser(users, passwordEncoder, "Dr. Karim Hossain", "karim.hossain@health.test", UserRole.DOCTOR, "+8801700000011");
            User doctor4 = createUser(users, passwordEncoder, "Dr. Nusrat Jahan", "nusrat.jahan@health.test", UserRole.DOCTOR, "+8801700000012");
            User doctor5 = createUser(users, passwordEncoder, "Dr. Rafiq Ahmed", "rafiq.ahmed@health.test", UserRole.DOCTOR, "+8801700000013");

            User patient2 = createUser(users, passwordEncoder, "Tanvir Alam", "tanvir.alam@health.test", UserRole.PATIENT, "+8801700000020");
            User patient3 = createUser(users, passwordEncoder, "Sadia Karim", "sadia.karim@health.test", UserRole.PATIENT, "+8801700000021");

            createProfileIfMissing(profiles, admin, "Road 1, Dhaka", "Dhaka", "Platform admin profile.", null, null, "Platform Admin", false, 23.8103, 90.4125);
            createProfileIfMissing(profiles, hospital, "Road 4, Dhaka", "Dhaka", "Multi-speciality hospital.", null, "HOSP-1001", "Multi-speciality Hospital", true, 23.8403, 90.4425);
            createProfileIfMissing(profiles, pharmacy, "Road 5, Dhaka", "Dhaka", "24/7 pharmacy service.", null, "PHAR-1001", "24/7 Pharmacy", true, 23.8503, 90.4525);
            createProfileIfMissing(profiles, diagnostic, "Road 6, Dhaka", "Dhaka", "Diagnostic centre service.", null, "DIAG-1001", "Diagnostics", false, 23.8603, 90.4625);
            createProfileIfMissing(profiles, ambulance, "Road 7, Dhaka", "Dhaka", "Emergency ambulance service.", null, "AMB-1001", "Emergency Ambulance", true, 23.8703, 90.4725);

            createDoctorProfileIfMissing(profiles, doctor, "Road 3, Dhaka", "Dhaka",
                    "Senior cardiologist with 12 years of experience treating heart disease.",
                    "Cardiology", "DOC-1001", "MBBS, FCPS (Cardiology)", 12, 1200.0, 23.8303, 90.4325);
            createDoctorProfileIfMissing(profiles, doctor2, "Road 8, Dhaka", "Dhaka",
                    "Neurologist treating headaches, stroke, and nervous system disorders.",
                    "Neurology", "DOC-1002", "MBBS, FCPS (Neurology)", 9, 1300.0, 23.8803, 90.4825);
            createDoctorProfileIfMissing(profiles, doctor3, "Road 9, Chattogram", "Chattogram",
                    "Dermatologist specializing in skin allergies and cosmetic dermatology.",
                    "Dermatology", "DOC-1003", "MBBS, DDV", 8, 800.0, 22.3569, 91.7832);
            createDoctorProfileIfMissing(profiles, doctor4, "Road 10, Dhaka", "Dhaka",
                    "Pediatrician with a focus on newborn and child care.",
                    "Pediatrics", "DOC-1004", "MBBS, DCH", 10, 700.0, 23.8903, 90.4925);
            createDoctorProfileIfMissing(profiles, doctor5, "Road 11, Sylhet", "Sylhet",
                    "Orthopedic surgeon experienced in joint replacement and sports injuries.",
                    "Orthopedics", "DOC-1005", "MBBS, MS (Ortho)", 15, 1500.0, 24.8949, 91.8687);

            createPatientProfileIfMissing(profiles, patient, "Road 2, Dhaka", "Dhaka", "Patient profile.",
                    LocalDate.of(1998, 9, 23), "Female", "O+", 23.8203, 90.4225);
            createPatientProfileIfMissing(profiles, patient2, "Road 12, Dhaka", "Dhaka", null,
                    LocalDate.of(1990, 1, 30), "Male", "A-", null, null);
            createPatientProfileIfMissing(profiles, patient3, "Road 13, Dhaka", "Dhaka", null,
                    LocalDate.of(1995, 4, 12), "Female", "B+", null, null);

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
                Medicine[] seededMedicines = {
                        new Medicine("Paracetamol 500mg", "Pain relief and fever reducer", new BigDecimal("2.50"), 120),
                        new Medicine("Amoxicillin 250mg", "Antibiotic capsules", new BigDecimal("8.00"), 45),
                        new Medicine("Vitamin C 1000mg", "Immune support tablets", new BigDecimal("5.50"), 8),
                        new Medicine("Cough Syrup", "Dry cough relief", new BigDecimal("6.75"), 30)
                };
                for (Medicine medicine : seededMedicines) {
                    medicine.setPharmacy(pharmacy);   // per-pharmacy catalogue
                    medicines.save(medicine);
                }
            }

            if (campaigns.count() == 0) {
                campaigns.save(new Campaign(
                        "Help Rahim Fight Kidney Disease",
                        "Rahim needs urgent dialysis treatment and cannot afford the ongoing cost. Every contribution helps.",
                        "Medical",
                        new BigDecimal("100000"),
                        admin
                ));
                campaigns.save(new Campaign(
                        "Flood Relief for Sylhet Families",
                        "Providing food, clean water, and temporary shelter to families affected by recent flooding.",
                        "Emergency",
                        new BigDecimal("250000"),
                        admin
                ));
                campaigns.save(new Campaign(
                        "Free Health Camp for Underprivileged Children",
                        "Funding a free health checkup and vaccination camp for children in underserved communities.",
                        "Community",
                        new BigDecimal("60000"),
                        admin
                ));
            }

            // Hospital & Diagnostic Module (Member 3): Bed / Doctor / Service
            // availability for the seeded hospital, and Test offers for the
            // seeded diagnostic centre.
            if (beds.findByHospitalIdOrderByWardType(hospital.getId()).isEmpty()) {
                beds.save(new BedAvailability(hospital, "General Ward", 60, 22));
                beds.save(new BedAvailability(hospital, "ICU", 12, 3));
                beds.save(new BedAvailability(hospital, "Cabin", 20, 8));
            }

            if (doctorAvailabilities.findByHospitalIdOrderByDayOfWeekAscStartTimeAsc(hospital.getId()).isEmpty()) {
                doctorAvailabilities.save(new HospitalDoctorAvailability(hospital, doctor, "MON",
                        LocalTime.of(9, 0), LocalTime.of(13, 0), "Cardiology - Room 204"));
                doctorAvailabilities.save(new HospitalDoctorAvailability(hospital, doctor, "WED",
                        LocalTime.of(9, 0), LocalTime.of(13, 0), "Cardiology - Room 204"));
                doctorAvailabilities.save(new HospitalDoctorAvailability(hospital, doctor2, "TUE",
                        LocalTime.of(14, 0), LocalTime.of(18, 0), "Neurology - Room 118"));
            }

            if (hospitalServices.findByHospitalIdOrderByServiceName(hospital.getId()).isEmpty()) {
                hospitalServices.save(new HospitalServiceOffering(hospital, "Emergency Care", "24/7 emergency department.", null));
                hospitalServices.save(new HospitalServiceOffering(hospital, "Surgery", "General and specialist surgery.", new BigDecimal("15000.00")));
                hospitalServices.save(new HospitalServiceOffering(hospital, "Maternity Ward", "Prenatal, delivery, and postnatal care.", new BigDecimal("25000.00")));
            }

            if (testOffers.findByDiagnosticCenterIdOrderByTestName(diagnostic.getId()).isEmpty()) {
                testOffers.save(new TestOffer(diagnostic, "Complete Blood Count (CBC)", "Full blood panel.", new BigDecimal("500.00"), "6 hours"));
                testOffers.save(new TestOffer(diagnostic, "X-Ray", "Digital X-ray imaging.", new BigDecimal("800.00"), "1 hour"));
                testOffers.save(new TestOffer(diagnostic, "MRI Scan", "Full-body or targeted MRI.", new BigDecimal("8000.00"), "24 hours"));
            }

            createSettingIfMissing(settings, "appointment_reminders", "enabled");
            createSettingIfMissing(settings, "ai_agent_status", "planned");
            createSettingIfMissing(settings, "payment_gateway", "sandbox");

            if (faqs.count() == 0) {
                faqs.save(new Faq("What is SmartCare?", "SmartCare is an integrated healthcare platform that connects patients with doctors, hospitals, pharmacies, diagnostic centres and ambulance teams.", 1));
                faqs.save(new Faq("Who can register, and do I need approval?", "Patients can use their accounts right away. Provider accounts are reviewed by an administrator before they are activated.", 2));
                faqs.save(new Faq("How do I book an appointment?", "Sign in as a patient, open Doctors, choose a provider, and select an available appointment slot.", 3));
                faqs.save(new Faq("Is my health data protected?", "Access is role-based and health records are only available to you and doctors you explicitly authorize.", 4));
                faqs.save(new Faq("How do I get more help?", "Contact the support team at support@smartcare.local for help with your account or the platform.", 5));
            }

            if (donors.count() == 0) {
                donors.save(new Donor(null, "Amina Rahman", "O+", "+8801711111111", "Dhaka", null, true));
                donors.save(new Donor(null, "Sakib Hasan", "A+", "+8801722222222", "Dhaka", null, true));
                donors.save(new Donor(null, "Nusrat Jahan", "B+", "+8801733333333", "Chattogram", null, true));
            }
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
            ProfileRepository profiles, User user, String address, String city, String bio,
            String specialization, String licenseNumber, String serviceName,
            boolean emergencyAvailable, Double latitude, Double longitude
    ) {
        if (profiles.findByUserId(user.getId()).isEmpty()) {
            profiles.save(new Profile(user, address, city, bio, specialization, licenseNumber, serviceName, emergencyAvailable, latitude, longitude));
        }
    }

    private void createDoctorProfileIfMissing(
            ProfileRepository profiles,
            User doctor,
            String address,
            String city,
            String bio,
            String specialization,
            String licenseNumber,
            String qualification,
            Integer experienceYears,
            Double consultationFee,
            Double latitude,
            Double longitude
    ) {
        if (profiles.findByUserId(doctor.getId()).isPresent()) {
            return;
        }
        Profile profile = new Profile(doctor, address, city, bio, specialization, licenseNumber, null, false, latitude, longitude);
        profile.setQualification(qualification);
        profile.setExperienceYears(experienceYears);
        profile.setConsultationFee(consultationFee);
        profiles.save(profile);
    }

    private void createPatientProfileIfMissing(
            ProfileRepository profiles,
            User patient,
            String address,
            String city,
            String bio,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            Double latitude,
            Double longitude
    ) {
        if (profiles.findByUserId(patient.getId()).isPresent()) {
            return;
        }
        Profile profile = new Profile(patient, address, city, bio, null, null, null, false, latitude, longitude);
        profile.setDateOfBirth(dateOfBirth);
        profile.setGender(gender);
        profile.setBloodGroup(bloodGroup);
        profiles.save(profile);
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
