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

/**
 * Hospital & Diagnostic Module (Member 3) - Service availability.
 * A service a hospital offers (e.g. "Emergency Care", "Surgery", "Maternity Ward").
 */
@Entity
@Table(name = "hospital_services")
public class HospitalServiceOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private User hospital;

    @Column(name = "service_name", nullable = false, length = 120)
    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean available = true;

    public HospitalServiceOffering() {
    }

    public HospitalServiceOffering(User hospital, String serviceName, String description, BigDecimal price) {
        this.hospital = hospital;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public User getHospital() {
        return hospital;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
