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
 * A patient's paid booking of a hospital service or a diagnostic test. Created
 * when the patient completes the bKash/Bank checkout for that item, so the
 * facility can see its incoming (paid) bookings.
 */
@Entity
@Table(name = "service_bookings")
public class ServiceBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    // "HOSPITAL" or "DIAGNOSTIC"
    @Column(name = "provider_type", length = 20)
    private String providerType;

    @Column(name = "item_name", nullable = false, length = 160)
    private String itemName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String status = "PAID";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public ServiceBooking() {
    }

    public ServiceBooking(User patient, User provider, String providerType, String itemName, BigDecimal amount) {
        this.patient = patient;
        this.provider = provider;
        this.providerType = providerType;
        this.itemName = itemName;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public User getPatient() { return patient; }
    public User getProvider() { return provider; }
    public String getProviderType() { return providerType; }
    public String getItemName() { return itemName; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
