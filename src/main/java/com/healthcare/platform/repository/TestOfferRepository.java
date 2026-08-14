package com.healthcare.platform.repository;

import com.healthcare.platform.model.TestOffer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestOfferRepository extends JpaRepository<TestOffer, Long> {
    List<TestOffer> findByDiagnosticCenterIdOrderByTestName(Long diagnosticCenterId);

    List<TestOffer> findByDiagnosticCenterIdAndAvailableTrueOrderByTestName(Long diagnosticCenterId);
}
