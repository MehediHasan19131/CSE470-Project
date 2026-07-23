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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Sprint 2 — Member 4 (Ambulance Service + Map).
 * A single vehicle owned/operated by an AMBULANCE-role account (the "provider").
 * A provider can own several ambulances (ride-sharing style fleet).
 */
@Entity
@Table(name = "ambulances")
public class Ambulance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @Column(name = "vehicle_number", nullable = false, length = 40)
    private String vehicleNumber;

    @Column(name = "vehicle_type", nullable = false, length = 40)
    private String vehicleType = "BASIC";

    private int capacity = 1;

    @Column(name = "driver_name", length = 120)
    private String driverName;

    @Column(name = "driver_phone", length = 40)
    private String driverPhone;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    private Double latitude;
    private Double longitude;

    @Column(name = "base_fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFare = new BigDecimal("300.00");

    @Column(name = "per_km_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal perKmRate = new BigDecimal("40.00");

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Ambulance() {
    }

    public Ambulance(User provider, String vehicleNumber, String vehicleType, int capacity,
                      String driverName, String driverPhone, boolean available, Double latitude, Double longitude) {
        this.provider = provider;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.capacity = capacity;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.available = available;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public User getProvider() {
        return provider;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public BigDecimal getPerKmRate() {
        return perKmRate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
