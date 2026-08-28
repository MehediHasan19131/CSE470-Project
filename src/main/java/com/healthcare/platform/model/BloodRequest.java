package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
public class BloodRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;
    @Column(nullable = false, length = 100) private String requesterName;
    @Column(nullable = false, length = 20) private String requesterPhone;
    @Column(nullable = false, length = 5) private String bloodGroupNeeded;
    @Column(nullable = false) private int unitsNeeded;
    @Column(length = 200) private String hospitalOrLocation;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private DonorUrgency urgency;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private BloodRequestStatus status = BloodRequestStatus.OPEN;
    @Column(nullable = false) private LocalDate neededByDate;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public BloodRequest() { }
    public BloodRequest(User requestedBy, String requesterName, String requesterPhone, String bloodGroupNeeded, int unitsNeeded,
                        String hospitalOrLocation, DonorUrgency urgency, LocalDate neededByDate) {
        this.requestedBy = requestedBy; this.requesterName = requesterName; this.requesterPhone = requesterPhone;
        this.bloodGroupNeeded = bloodGroupNeeded; this.unitsNeeded = unitsNeeded; this.hospitalOrLocation = hospitalOrLocation;
        this.urgency = urgency; this.neededByDate = neededByDate;
    }
    public Long getId() { return id; }
    public User getRequestedBy() { return requestedBy; }
    public String getRequesterName() { return requesterName; }
    public String getRequesterPhone() { return requesterPhone; }
    public String getBloodGroupNeeded() { return bloodGroupNeeded; }
    public int getUnitsNeeded() { return unitsNeeded; }
    public String getHospitalOrLocation() { return hospitalOrLocation; }
    public DonorUrgency getUrgency() { return urgency; }
    public BloodRequestStatus getStatus() { return status; }
    public void setStatus(BloodRequestStatus status) { this.status = status; }
    public LocalDate getNeededByDate() { return neededByDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
