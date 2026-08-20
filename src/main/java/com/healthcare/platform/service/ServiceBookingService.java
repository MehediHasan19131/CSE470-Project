package com.healthcare.platform.service;

import com.healthcare.platform.model.ServiceBooking;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.UserRole;
import com.healthcare.platform.repository.ServiceBookingRepository;
import com.healthcare.platform.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Paid bookings of hospital services / diagnostic tests. Created after the
 * patient completes the bKash/Bank checkout for that item.
 */
@Service
public class ServiceBookingService {

    private final ServiceBookingRepository bookings;
    private final UserRepository users;

    public ServiceBookingService(ServiceBookingRepository bookings, UserRepository users) {
        this.bookings = bookings;
        this.users = users;
    }

    public ServiceBooking book(User patient, Long providerId, String itemName, BigDecimal amount) {
        User provider = users.findById(providerId)
                .orElseThrow(() -> new NoSuchElementException("Facility not found."));
        String providerType = provider.getRole() == UserRole.DIAGNOSTIC ? "DIAGNOSTIC" : "HOSPITAL";
        return bookings.save(new ServiceBooking(patient, provider, providerType, itemName, amount));
    }

    @Transactional(readOnly = true)
    public List<ServiceBooking> forProvider(Long providerId) {
        return bookings.findByProviderIdOrderByCreatedAtDesc(providerId);
    }

    @Transactional(readOnly = true)
    public List<ServiceBooking> forPatient(Long patientId) {
        return bookings.findByPatientIdOrderByCreatedAtDesc(patientId);
    }
}
