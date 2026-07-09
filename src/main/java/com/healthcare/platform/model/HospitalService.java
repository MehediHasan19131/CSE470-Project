package com.healthcare.platform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hospital_services")
public class HospitalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_user_id", nullable = false)
    private User hospital;

    private String serviceName;
    private boolean available;

    public HospitalService() {
    }

    public HospitalService(User hospital, String serviceName, boolean available) {
        this.hospital = hospital;
        this.serviceName = serviceName;
        this.available = available;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isAvailable() {
        return available;
    }
}
