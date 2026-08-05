package com.healthcare.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String address;
    private String city;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String specialization;
    private String licenseNumber;
    private String serviceName;
    private boolean emergencyAvailable;
    private Double latitude;
    private Double longitude;

<<<<<<< HEAD
    public Profile() {}
=======
    // Added for the Doctor & Patient Module (Sprint 1 - Imtiaz Zaman Sami).
    // Nullable so existing roles (admin, hospital, pharmacy, etc.) are unaffected.
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "consultation_fee")
    private Double consultationFee;

    public Profile() {
    }
>>>>>>> origin/sami-sprint1

    public Profile(User user, String address, String city, String bio, String specialization,
                   String licenseNumber, String serviceName, boolean emergencyAvailable,
                   Double latitude, Double longitude) {
        this.user = user;
        this.address = address;
        this.city = city;
        this.bio = bio;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.serviceName = serviceName;
        this.emergencyAvailable = emergencyAvailable;
        this.latitude = latitude;
        this.longitude = longitude;
    }

<<<<<<< HEAD
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getBio() { return bio; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public boolean isEmergencyAvailable() { return emergencyAvailable; }
    public void setEmergencyAvailable(boolean emergencyAvailable) { this.emergencyAvailable = emergencyAvailable; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
=======
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getBio() {
        return bio;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isEmergencyAvailable() {
        return emergencyAvailable;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setEmergencyAvailable(boolean emergencyAvailable) {
        this.emergencyAvailable = emergencyAvailable;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }
>>>>>>> origin/sami-sprint1
}
