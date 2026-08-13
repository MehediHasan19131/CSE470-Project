package com.healthcare.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Sprint 4 - BloodRequest.
 * A request for blood units. The requester need not be a registered user.
 */
@Entity
@Table(name = "blood_requests")
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String requesterName;

    @Column(nullable = false, length = 20)
    private String requesterPhone;

    @Column(nullable = false, length = 5)
    private String bloodGroupNeeded; // A+, A-, B+, B-, AB+, AB-, O+, O-

    @Column(nullable = false)
    private int unitsNeeded;

    @Column(length = 200)
    private String hospitalOrLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DonorUrgency urgency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BloodRequestStatus status = BloodRequestStatus.OPEN;

    private LocalDate neededByDate;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public BloodRequest() {}

    public BloodRequest(String requesterName, String requesterPhone,
                        String bloodGroupNeeded, int unitsNeeded,
                        String hospitalOrLocation, DonorUrgency urgency,
                        LocalDate neededByDate) {
        this.requesterName = requesterName;
        this.requesterPhone = requesterPhone;
        this.bloodGroupNeeded = bloodGroupNeeded;
        this.unitsNeeded = unitsNeeded;
        this.hospitalOrLocation = hospitalOrLocation;
        this.urgency = urgency;
        this.neededByDate = neededByDate;
    }

    public boolean isValidBloodGroup() {
        return Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-").contains(bloodGroupNeeded);
    }

    public boolean isValidUnits() { return unitsNeeded > 0; }
    public boolean isNeededByDateFuture() {
        return neededByDate == null || !neededByDate.isBefore(LocalDate.now());
    }

    public Long getId() { return id; }
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public String getRequesterPhone() { return requesterPhone; }
    public void setRequesterPhone(String requesterPhone) { this.requesterPhone = requesterPhone; }
    public String getBloodGroupNeeded() { return bloodGroupNeeded; }
    public void setBloodGroupNeeded(String bloodGroupNeeded) { this.bloodGroupNeeded = bloodGroupNeeded; }
    public int getUnitsNeeded() { return unitsNeeded; }
    public void setUnitsNeeded(int unitsNeeded) { this.unitsNeeded = unitsNeeded; }
    public String getHospitalOrLocation() { return hospitalOrLocation; }
    public void setHospitalOrLocation(String hospitalOrLocation) { this.hospitalOrLocation = hospitalOrLocation; }
    public DonorUrgency getUrgency() { return urgency; }
    public void setUrgency(DonorUrgency urgency) { this.urgency = urgency; }
    public BloodRequestStatus getStatus() { return status; }
    public void setStatus(BloodRequestStatus status) { this.status = status; }
    public LocalDate getNeededByDate() { return neededByDate; }
    public void setNeededByDate(LocalDate neededByDate) { this.neededByDate = neededByDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}