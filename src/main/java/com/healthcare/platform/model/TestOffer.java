package com.healthcare.platform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "test_offers")
public class TestOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_user_id", nullable = false)
    private User hospital;

    private String testName;
    private BigDecimal regularPrice;
    private BigDecimal offerPrice;
    private int discountPercent;

    public TestOffer() {
    }

    public TestOffer(User hospital, String testName, BigDecimal regularPrice, BigDecimal offerPrice, int discountPercent) {
        this.hospital = hospital;
        this.testName = testName;
        this.regularPrice = regularPrice;
        this.offerPrice = offerPrice;
        this.discountPercent = discountPercent;
    }

    public String getTestName() {
        return testName;
    }

    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    public BigDecimal getOfferPrice() {
        return offerPrice;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }
}
