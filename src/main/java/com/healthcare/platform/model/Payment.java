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
 * Online Payment feature (feature #14). A single payment made through the
 * in-app bKash / Bank-Card checkout. This is a self-contained sandbox ledger:
 * no real gateway is contacted - PaymentService always records a SUCCESS with a
 * generated transaction id (mirrors the seeded app_settings "payment_gateway" =
 * "sandbox"). For a donation, a Payment row is stored alongside the Donation.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // "bKash" or "Bank Card"
    @Column(name = "method", nullable = false, length = 40)
    private String method;

    @Column(name = "purpose", length = 160)
    private String purpose;

    // "DONATION" or "GENERAL" - what the payment was for.
    @Column(name = "reference_type", length = 30)
    private String referenceType;

    // e.g. the campaign id for a donation payment (nullable).
    @Column(name = "reference_id")
    private Long referenceId;

    // Masked sender account / mobile number, e.g. "017XXXXX678".
    @Column(name = "sender_account", length = 40)
    private String senderAccount;

    @Column(name = "transaction_id", nullable = false, length = 60)
    private String transactionId;

    // "SUCCESS" or "FAILED"
    @Column(name = "status", nullable = false, length = 20)
    private String status = "SUCCESS";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Payment() {
    }

    public Payment(User payer, BigDecimal amount, String method, String purpose,
                   String referenceType, Long referenceId, String senderAccount,
                   String transactionId, String status) {
        this.payer = payer;
        this.amount = amount;
        this.method = method;
        this.purpose = purpose;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.senderAccount = senderAccount;
        this.transactionId = transactionId;
        this.status = status;
    }

    public Long getId() { return id; }
    public User getPayer() { return payer; }
    public BigDecimal getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getPurpose() { return purpose; }
    public String getReferenceType() { return referenceType; }
    public Long getReferenceId() { return referenceId; }
    public String getSenderAccount() { return senderAccount; }
    public String getTransactionId() { return transactionId; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
