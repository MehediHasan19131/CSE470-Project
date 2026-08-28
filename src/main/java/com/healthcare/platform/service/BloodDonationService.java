package com.healthcare.platform.service;

import com.healthcare.platform.model.*;
import com.healthcare.platform.repository.BloodRequestRepository;
import com.healthcare.platform.repository.DonorRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BloodDonationService {
    public static final List<String> BLOOD_GROUPS = List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    private final DonorRepository donors;
    private final BloodRequestRepository requests;

    public BloodDonationService(DonorRepository donors, BloodRequestRepository requests) {
        this.donors = donors; this.requests = requests;
    }

    public List<Donor> donors(String bloodGroup, String city) {
        boolean byGroup = bloodGroup != null && !bloodGroup.isBlank();
        boolean byCity = city != null && !city.isBlank();
        if (byGroup && !BLOOD_GROUPS.contains(bloodGroup)) throw new IllegalArgumentException("Choose a valid blood group.");
        if (byGroup && byCity) return donors.findByAvailableTrueAndBloodGroupAndCityIgnoreCaseOrderByCreatedAtDesc(bloodGroup, city.trim());
        if (byGroup) return donors.findByAvailableTrueAndBloodGroupOrderByCreatedAtDesc(bloodGroup);
        if (byCity) return donors.findByAvailableTrueAndCityIgnoreCaseOrderByCreatedAtDesc(city.trim());
        return donors.findByAvailableTrueOrderByCreatedAtDesc();
    }

    public void registerDonor(User user, String fullName, String bloodGroup, String phone, String city, LocalDate lastDonationDate, boolean available) {
        if (fullName == null || fullName.isBlank() || phone == null || phone.isBlank() || !BLOOD_GROUPS.contains(bloodGroup)) {
            throw new IllegalArgumentException("Name, phone number, and a valid blood group are required.");
        }
        donors.save(new Donor(user, fullName.trim(), bloodGroup, phone.trim(), city == null ? "" : city.trim(), lastDonationDate, available));
    }

    public List<BloodRequest> openRequests() {
        return requests.findByStatusAndNeededByDateGreaterThanEqualOrderByUrgencyDescCreatedAtDesc(BloodRequestStatus.OPEN, LocalDate.now());
    }

    public BloodRequest getRequest(Long id) { return requests.findById(id).orElseThrow(() -> new IllegalArgumentException("Blood request not found.")); }

    public void createRequest(User user, String requesterName, String requesterPhone, String bloodGroup, int units,
                              String location, DonorUrgency urgency, LocalDate neededBy) {
        if (requesterName == null || requesterName.isBlank() || requesterPhone == null || requesterPhone.isBlank()
                || !BLOOD_GROUPS.contains(bloodGroup) || units < 1 || neededBy == null || neededBy.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Enter valid request details. The needed-by date cannot be in the past.");
        }
        requests.save(new BloodRequest(user, requesterName.trim(), requesterPhone.trim(), bloodGroup, units,
                location == null ? "" : location.trim(), urgency, neededBy));
    }

    @Transactional
    public void fulfil(Long id, User currentUser) {
        BloodRequest request = getRequest(id);
        if (!request.getRequestedBy().getId().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Only the request owner or an administrator can update this request.");
        }
        request.setStatus(BloodRequestStatus.FULFILLED);
    }
}
