package com.healthcare.platform.repository;

import com.healthcare.platform.model.BloodRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    @Query("SELECT br FROM BloodRequest br WHERE br.status = :status ORDER BY br.createdAt DESC")
    List<BloodRequest> findByStatus(@Param("status") String status);

    @Query("SELECT br FROM BloodRequest br WHERE br.status = 'OPEN' AND br.neededByDate >= CURRENT_DATE ORDER BY br.urgency, br.createdAt DESC")
    List<BloodRequest> findOpenRequests();

    @Query("SELECT br FROM BloodRequest br WHERE br.bloodGroupNeeded = :bloodGroup AND br.status = 'OPEN'")
    List<BloodRequest> findByBloodGroupOpen(@Param("bloodGroup") String bloodGroup);

    @Modifying
    @Query("UPDATE BloodRequest br SET br.status = :newStatus WHERE br.id = :id")
    void updateStatus(@Param("id") Long id, @Param("newStatus") String newStatus);
}