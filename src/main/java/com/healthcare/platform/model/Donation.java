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
 * Crowdfunding & Payment Module (Sprint 4) - Imtiaz Zaman Sami (23101551)
 * A single donation made to a Campaign, including its (simulated) payment details.
 */
@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String message;

    // Payment APIs: simulated payment details (sandbox - see app_settings "payment_gateway")
    @Column(name = "payment_method", length = 40)
    private String paymentMethod;

    @Column(name = "transaction_id", length = 60)
    private String transactionId;

    // "SUCCESS", "FAILED"
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "SUCCESS";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Donation() {
    }

    public Donation(Campaign campaign, User donor, BigDecimal amount, String message, String paymentMethod, String transactionId, String paymentStatus) {
        this.campaign = campaign;
        this.donor = donor;
        this.amount = amount;
        this.message = message;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.paymentStatus = paymentStatus;
    }

    public Long getId() {
        return id;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public User getDonor() {
        return donor;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
