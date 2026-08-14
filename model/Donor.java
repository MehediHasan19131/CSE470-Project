package com.healthcare.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.util.List;

/**
 * Sprint 4 - Donor.
 *
 * user FK is nullable: a donor may or may not be a registered platform user.
 * If user IS set, the donor profile is tied to that account.
 * If user IS NULL, the donor is a stand-alone volunteer record.
 */
@Entity
@Table(name = "donors")
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 5)
    private String bloodGroup; // A+, A-, B+, B-, AB+, AB-, O+, O-

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String city;

    private LocalDate lastDonationDate;

    @Column(nullable = false)
    private boolean isAvailable = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Donor() {}

    public Donor(User user, String fullName, String bloodGroup, String phone,
                 String city, LocalDate lastDonationDate, boolean isAvailable) {
        this.user = user;
        this.fullName = fullName;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.city = city;
        this.lastDonationDate = lastDonationDate;
        this.isAvailable = isAvailable;
    }

    public List<String> bloodGroupList() {
        return Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    }

    public boolean isValidBloodGroup() {
        return bloodGroupList().contains(bloodGroup);
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public void setLastDonationDate(LocalDate lastDonationDate) { this.lastDonationDate = lastDonationDate; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}