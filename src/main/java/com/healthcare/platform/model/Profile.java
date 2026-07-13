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

    public Profile() {}

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
}
