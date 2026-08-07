package com.healthcare.platform.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.healthcare.platform.model.TestOffer;

public interface TestOfferRepository extends JpaRepository<TestOffer, Long> {
    List<TestOffer> findByHospitalIdOrderByTestNameAsc(Long hospitalId);
}
