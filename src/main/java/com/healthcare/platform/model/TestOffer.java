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
 * Hospital & Diagnostic Module (Member 3) - Diagnostic Center test offers.
 * A test a diagnostic centre offers (e.g. "Complete Blood Count", "MRI Scan").
 */
@Entity
@Table(name = "test_offers")
public class TestOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnostic_center_id", nullable = false)
    private User diagnosticCenter;

    @Column(name = "test_name", nullable = false, length = 120)
    private String testName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "turnaround_time", length = 60)
    private String turnaroundTime;

    @Column(nullable = false)
    private boolean available = true;

    public TestOffer() {
    }

    public TestOffer(User diagnosticCenter, String testName, String description, BigDecimal price, String turnaroundTime) {
        this.diagnosticCenter = diagnosticCenter;
        this.testName = testName;
        this.description = description;
        this.price = price;
        this.turnaroundTime = turnaroundTime;
    }

    public Long getId() {
        return id;
    }

    public User getDiagnosticCenter() {
        return diagnosticCenter;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
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

    public String getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(String turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
