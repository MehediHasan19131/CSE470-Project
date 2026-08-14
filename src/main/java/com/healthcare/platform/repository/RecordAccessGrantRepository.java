package com.healthcare.platform.repository;

import com.healthcare.platform.model.RecordAccessGrant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordAccessGrantRepository extends JpaRepository<RecordAccessGrant, Long> {
    List<RecordAccessGrant> findByPatientIdOrderByUpdatedAtDesc(Long patientId);
    List<RecordAccessGrant> findByDoctorIdAndActiveTrueOrderByUpdatedAtDesc(Long doctorId);
    Optional<RecordAccessGrant> findByPatientIdAndDoctorId(Long patientId, Long doctorId);
    boolean existsByPatientIdAndDoctorIdAndActiveTrue(Long patientId, Long doctorId);

    // Called from AdminUserService.deleteUser(...) before deleting the user row -
    // record_access_grants has a foreign key on users.id for both patient_id and
    // doctor_id, so either side of a grant would block the delete otherwise.
    void deleteByPatientId(Long patientId);
    void deleteByDoctorId(Long doctorId);
}
