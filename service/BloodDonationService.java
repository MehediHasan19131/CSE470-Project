package com.healthcare.platform.service.sprint4;

import com.healthcare.platform.model.BloodRequest;
import com.healthcare.platform.model.Donor;
import com.healthcare.platform.model.User;
import com.healthcare.platform.model.DonorUrgency;
import com.healthcare.platform.model.BloodRequestStatus;
import com.healthcare.platform.repository.BloodRequestRepository;
import com.healthcare.platform.repository.DonorRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BloodDonationService {

    private final DonorRepository donors;
    private final BloodRequestRepository requests;

    public BloodDonationService(DonorRepository donors, BloodRequestRepository requests) {
        this.donors = donors;
        this.requests = requests;
    }

    // ---------- Donor CRUD ----------
    public Donor create(User user, String fullName, String bloodGroup, String phone,
                        String city, LocalDate lastDonationDate, Boolean isAvailable) {
        boolean available = isAvailable != null && isAvailable;
        Donor donor = new Donor(user, fullName, bloodGroup, phone, city, lastDonationDate, available);
        if (donor.isValidBloodGroup()) {
            return donors.save(donor);
        }
        throw new IllegalArgumentException("Invalid blood group.");
    }

    public List<Donor> allAvailable() {
        return donors.findAllAvailable();
    }

    public List<Donor> matchingDonors(String bloodGroup, String city) {
        return donors.findMatchingDonors(bloodGroup, city);
    }

    public List<Donor> matchingDonors(String bloodGroup, String city, Long excludeUserId) {
        return donors.findMatchingDonorsExcluding(bloodGroup, city, excludeUserId);
    }

    public Donor getDonor(Long id, User currentUser) {
        Donor d = donors.findById(id).orElseThrow(() -> new RuntimeException("Donor not found"));
        if (!d.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Not authorized");
        }
        return d;
    }

    // ---------- BloodRequest CRUD ----------
    public BloodRequest create(String requesterName, String requesterPhone,
                               String bloodGroupNeeded, int unitsNeeded,
                               String hospitalOrLocation, DonorUrgency urgency,
                               LocalDate neededByDate) {
        validateCreate(requesterName, requesterPhone, bloodGroupNeeded, unitsNeeded, neededByDate);
        BloodRequest req = new BloodRequest(requesterName, requesterPhone,
                bloodGroupNeeded, unitsNeeded, hospitalOrLocation, urgency, neededByDate);
        req.setStatus(BloodRequestStatus.OPEN);
        return requests.save(req);
    }

    public List<BloodRequest> openRequests() {
        return requests.findOpenRequests();
    }

    public List<BloodRequest> byBloodGroupOpen(String bloodGroup) {
        return requests.findByBloodGroupOpen(bloodGroup);
    }

    public BloodRequest getRequest(Long id, User currentUser) {
        BloodRequest br = requests.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        if (!br.getRequesterName().equals(currentUser.getFullName())
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Not authorized");
        }
        return br;
    }

    @Transactional
    public void markFulfilled(Long id, User currentUser) {
        BloodRequest br = requests.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        if (br.getRequesterName().equals(currentUser.getFullName())
                || currentUser.getRole().name().equals("ADMIN")) {
            br.setStatus(BloodRequestStatus.FULFILLED);
            br.setUpdatedAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void markExpired(Long id) {
        BloodRequest br = requests.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        if (br.getStatus() != BloodRequestStatus.FULFILLED) {
            br.setStatus(BloodRequestStatus.EXPIRED);
            br.setUpdatedAt(LocalDateTime.now());
        }
    }

    private void validateCreate(String requesterName, String requesterPhone,
                                String bloodGroupNeeded, int unitsNeeded,
                                LocalDate neededByDate) {
        if (requesterName == null || requesterName.isBlank()) {
            throw new IllegalArgumentException("Requester name is required.");
        }
        if (requesterPhone == null || requesterPhone.isBlank()) {
            throw new IllegalArgumentException("Requester phone is required.");
        }
        if (bloodGroupNeeded == null
                || !Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-").contains(bloodGroupNeeded)) {
            throw new IllegalArgumentException("Valid blood group is required.");
        }
        if (unitsNeeded <= 0) {
            throw new IllegalArgumentException("Units needed must be greater than 0.");
        }
        if (neededByDate != null && neededByDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("neededByDate must be today or in the future.");
        }
    }
}