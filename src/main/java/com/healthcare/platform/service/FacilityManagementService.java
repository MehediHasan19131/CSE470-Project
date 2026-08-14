package com.healthcare.platform.service;

import com.healthcare.platform.model.BedAvailability;
import com.healthcare.platform.model.HospitalDoctorAvailability;
import com.healthcare.platform.model.HospitalServiceOffering;
import com.healthcare.platform.model.TestOffer;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.BedAvailabilityRepository;
import com.healthcare.platform.repository.HospitalDoctorAvailabilityRepository;
import com.healthcare.platform.repository.HospitalServiceOfferingRepository;
import com.healthcare.platform.repository.TestOfferRepository;
import com.healthcare.platform.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

/**
 * Hospital & Diagnostic Module (Member 3).
 * <p>
 * Business rules behind the three Hospital-owner tools (Bed availability,
 * Doctor availability, Service availability) and the Diagnostic-owner tool
 * (Test offers). Every write method takes the acting user and verifies they
 * own the row being changed - a hospital can't edit another hospital's beds,
 * and only ADMIN/the owning facility account may write here.
 */
@Service
public class FacilityManagementService {

    private final BedAvailabilityRepository beds;
    private final HospitalDoctorAvailabilityRepository doctorAvailabilities;
    private final HospitalServiceOfferingRepository services;
    private final TestOfferRepository testOffers;
    private final UserRepository users;

    public FacilityManagementService(BedAvailabilityRepository beds,
                                      HospitalDoctorAvailabilityRepository doctorAvailabilities,
                                      HospitalServiceOfferingRepository services,
                                      TestOfferRepository testOffers,
                                      UserRepository users) {
        this.beds = beds;
        this.doctorAvailabilities = doctorAvailabilities;
        this.services = services;
        this.testOffers = testOffers;
        this.users = users;
    }

    // ---------------------------------------------------------------------
    // Bed availability
    // ---------------------------------------------------------------------

    public List<BedAvailability> getBeds(Long hospitalId) {
        return beds.findByHospitalIdOrderByWardType(hospitalId);
    }

    public BedAvailability addBed(User hospital, String wardType, int totalBeds, int availableBeds) {
        requireHospital(hospital);
        return beds.save(new BedAvailability(hospital, wardType, clamp(totalBeds, availableBeds)[0], clamp(totalBeds, availableBeds)[1]));
    }

    public void updateBed(User hospital, Long id, String wardType, int totalBeds, int availableBeds) {
        BedAvailability bed = beds.findById(id).orElseThrow(() -> new NoSuchElementException("Bed record not found."));
        requireOwner(hospital, bed.getHospital().getId());
        int[] clamped = clamp(totalBeds, availableBeds);
        bed.setWardType(wardType);
        bed.setTotalBeds(clamped[0]);
        bed.setAvailableBeds(clamped[1]);
        bed.setUpdatedAt(java.time.LocalDateTime.now());
        beds.save(bed);
    }

    public void deleteBed(User hospital, Long id) {
        BedAvailability bed = beds.findById(id).orElseThrow(() -> new NoSuchElementException("Bed record not found."));
        requireOwner(hospital, bed.getHospital().getId());
        beds.delete(bed);
    }

    private int[] clamp(int total, int available) {
        int t = Math.max(0, total);
        int a = Math.max(0, Math.min(available, t));
        return new int[]{t, a};
    }

    // ---------------------------------------------------------------------
    // Doctor availability
    // ---------------------------------------------------------------------

    public List<HospitalDoctorAvailability> getDoctorAvailability(Long hospitalId) {
        return doctorAvailabilities.findByHospitalIdOrderByDayOfWeekAscStartTimeAsc(hospitalId);
    }

    /** Doctors a hospital can pick from - any account with role DOCTOR. */
    public List<User> availableDoctorsToAssign() {
        return users.findByRoleAndActiveTrue(UserRole.DOCTOR);
    }

    public HospitalDoctorAvailability addDoctorAvailability(User hospital, Long doctorId, String dayOfWeek,
                                                              LocalTime startTime, LocalTime endTime, String notes) {
        requireHospital(hospital);
        User doctor = users.findById(doctorId).orElseThrow(() -> new NoSuchElementException("Doctor not found."));
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new IllegalArgumentException("Selected user is not a doctor.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        return doctorAvailabilities.save(new HospitalDoctorAvailability(hospital, doctor, dayOfWeek, startTime, endTime, notes));
    }

    public void deleteDoctorAvailability(User hospital, Long id) {
        HospitalDoctorAvailability entry = doctorAvailabilities.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Availability entry not found."));
        requireOwner(hospital, entry.getHospital().getId());
        doctorAvailabilities.delete(entry);
    }

    // ---------------------------------------------------------------------
    // Service availability
    // ---------------------------------------------------------------------

    public List<HospitalServiceOffering> getServices(Long hospitalId) {
        return services.findByHospitalIdOrderByServiceName(hospitalId);
    }

    public HospitalServiceOffering addService(User hospital, String serviceName, String description, BigDecimal price) {
        requireHospital(hospital);
        return services.save(new HospitalServiceOffering(hospital, serviceName, description, price));
    }

    public void updateService(User hospital, Long id, String serviceName, String description, BigDecimal price, boolean available) {
        HospitalServiceOffering service = services.findById(id).orElseThrow(() -> new NoSuchElementException("Service not found."));
        requireOwner(hospital, service.getHospital().getId());
        service.setServiceName(serviceName);
        service.setDescription(description);
        service.setPrice(price);
        service.setAvailable(available);
        services.save(service);
    }

    public void deleteService(User hospital, Long id) {
        HospitalServiceOffering service = services.findById(id).orElseThrow(() -> new NoSuchElementException("Service not found."));
        requireOwner(hospital, service.getHospital().getId());
        services.delete(service);
    }

    // ---------------------------------------------------------------------
    // Test offers (Diagnostic Center)
    // ---------------------------------------------------------------------

    public List<TestOffer> getTestOffers(Long diagnosticCenterId) {
        return testOffers.findByDiagnosticCenterIdOrderByTestName(diagnosticCenterId);
    }

    public TestOffer addTestOffer(User diagnosticCenter, String testName, String description, BigDecimal price, String turnaroundTime) {
        requireDiagnostic(diagnosticCenter);
        return testOffers.save(new TestOffer(diagnosticCenter, testName, description, price, turnaroundTime));
    }

    public void updateTestOffer(User diagnosticCenter, Long id, String testName, String description,
                                 BigDecimal price, String turnaroundTime, boolean available) {
        TestOffer offer = testOffers.findById(id).orElseThrow(() -> new NoSuchElementException("Test offer not found."));
        requireOwner(diagnosticCenter, offer.getDiagnosticCenter().getId());
        offer.setTestName(testName);
        offer.setDescription(description);
        offer.setPrice(price);
        offer.setTurnaroundTime(turnaroundTime);
        offer.setAvailable(available);
        testOffers.save(offer);
    }

    public void deleteTestOffer(User diagnosticCenter, Long id) {
        TestOffer offer = testOffers.findById(id).orElseThrow(() -> new NoSuchElementException("Test offer not found."));
        requireOwner(diagnosticCenter, offer.getDiagnosticCenter().getId());
        testOffers.delete(offer);
    }

    // ---------------------------------------------------------------------

    private void requireHospital(User user) {
        if (user.getRole() != UserRole.HOSPITAL) {
            throw new IllegalStateException("Only a hospital account can manage this.");
        }
    }

    private void requireDiagnostic(User user) {
        if (user.getRole() != UserRole.DIAGNOSTIC) {
            throw new IllegalStateException("Only a diagnostic centre account can manage this.");
        }
    }

    private void requireOwner(User actor, Long ownerId) {
        if (actor.getRole() == UserRole.ADMIN) {
            return;
        }
        if (!actor.getId().equals(ownerId)) {
            throw new IllegalStateException("You don't own this record.");
        }
    }
}
