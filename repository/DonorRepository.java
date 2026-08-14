package com.healthcare.platform.repository;

import com.healthcare.platform.model.Donor;
import com.healthcare.platform.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    @Query("SELECT d FROM Donor d WHERE d.bloodGroup = :bloodGroup AND d.city = :city AND d.isAvailable = true")
    List<Donor> findMatchingDonors(@Param("bloodGroup") String bloodGroup, @Param("city") String city);

    @Query("SELECT d FROM Donor d WHERE d.bloodGroup = :bloodGroup AND d.city = :city AND d.isAvailable = true AND (d.user.id != :excludeUserId OR d.user IS NULL)")
    List<Donor> findMatchingDonorsExcluding(@Param("bloodGroup") String bloodGroup, @Param("city") String city, @Param("excludeUserId") Long excludeUserId);

    @Query("SELECT d FROM Donor d WHERE d.bloodGroup = :bloodGroup AND d.isAvailable = true")
    List<Donor> findByBloodGroup(@Param("bloodGroup") String bloodGroup);

    @Query("SELECT d FROM Donor d WHERE d.city = :city AND d.isAvailable = true")
    List<Donor> findByCity(@Param("city") String city);

    @Query("SELECT d FROM Donor d WHERE d.isAvailable = true")
    List<Donor> findAllAvailable();
}