package com.healthcare.platform.repository;

import com.healthcare.platform.model.Donor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    List<Donor> findByAvailableTrueOrderByCreatedAtDesc();
    List<Donor> findByAvailableTrueAndBloodGroupOrderByCreatedAtDesc(String bloodGroup);
    List<Donor> findByAvailableTrueAndCityIgnoreCaseOrderByCreatedAtDesc(String city);
    List<Donor> findByAvailableTrueAndBloodGroupAndCityIgnoreCaseOrderByCreatedAtDesc(String bloodGroup, String city);
}
