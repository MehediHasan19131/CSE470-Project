package com.healthcare.platform.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "donors")
public class Donor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false, length = 100) private String fullName;
    @Column(nullable = false, length = 5) private String bloodGroup;
    @Column(nullable = false, length = 20) private String phone;
    @Column(length = 100) private String city;
    private LocalDate lastDonationDate;
    @Column(nullable = false) private boolean available = true;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();

    public Donor() { }
    public Donor(User user, String fullName, String bloodGroup, String phone, String city, LocalDate lastDonationDate, boolean available) {
        this.user = user; this.fullName = fullName; this.bloodGroup = bloodGroup; this.phone = phone;
        this.city = city; this.lastDonationDate = lastDonationDate; this.available = available;
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getFullName() { return fullName; }
    public String getBloodGroup() { return bloodGroup; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public LocalDate getLastDonationDate() { return lastDonationDate; }
    public boolean isAvailable() { return available; }
}
