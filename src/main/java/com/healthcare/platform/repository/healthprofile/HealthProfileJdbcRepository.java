package com.healthcare.platform.repository.healthprofile;
import com.healthcare.platform.model.healthprofile.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Health Profile data access, written as plain JDBC (no Spring Data JPA, no
 * @Entity, no Hibernate) - every query below is hand-written SQL, and every
 * row is mapped by hand. Same "no ORM" rule Member 1 used for
 * {@code AuthUserJdbcRepository} and Member 2 used for {@code ReviewJdbcRepository},
 * applied to this sprint's `medical_history` and `allergies` tables.
 */
@Repository
public class HealthProfileJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public HealthProfileJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------------------------------------------------------------
    // Medical history ("Add History" / "Update History")
    // ---------------------------------------------------------------------

    private static final RowMapper<MedicalHistoryEntry> HISTORY_ROW_MAPPER = (rs, rowNum) -> {
        MedicalHistoryEntry entry = new MedicalHistoryEntry();
        entry.setId(rs.getLong("id"));
        entry.setPatientId(rs.getLong("patient_id"));
        entry.setCondition(rs.getString("condition_name"));
        Date diagnosedOn = rs.getDate("diagnosed_on");
        if (diagnosedOn != null) {
            entry.setDiagnosedOn(diagnosedOn.toLocalDate());
        }
        entry.setNotes(rs.getString("notes"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            entry.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            entry.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return entry;
    };

    public Optional<MedicalHistoryEntry> findHistoryById(Long id) {
        List<MedicalHistoryEntry> results = jdbcTemplate.query(
                "SELECT id, patient_id, condition_name, diagnosed_on, notes, created_at, updated_at " +
                        "FROM medical_history WHERE id = ?",
                HISTORY_ROW_MAPPER, id
        );
        return results.stream().findFirst();
    }

    /** Every history entry for one patient, most recently diagnosed first (nulls last), then most recently added. */
    public List<MedicalHistoryEntry> findHistoryByPatient(Long patientId) {
        return jdbcTemplate.query(
                "SELECT id, patient_id, condition_name, diagnosed_on, notes, created_at, updated_at " +
                        "FROM medical_history WHERE patient_id = ? " +
                        "ORDER BY diagnosed_on IS NULL, diagnosed_on DESC, created_at DESC",
                HISTORY_ROW_MAPPER, patientId
        );
    }

    public MedicalHistoryEntry insertHistory(MedicalHistoryEntry entry) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO medical_history (patient_id, condition_name, diagnosed_on, notes, created_at, updated_at) " +
                            "VALUES (?, ?, ?, ?, NOW(), NOW())",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, entry.getPatientId());
            ps.setString(2, entry.getCondition());
            if (entry.getDiagnosedOn() != null) {
                ps.setDate(3, Date.valueOf(entry.getDiagnosedOn()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, entry.getNotes());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        entry.setId(generatedId != null ? generatedId.longValue() : null);
        return entry;
    }

    public void updateHistory(Long id, String condition, java.time.LocalDate diagnosedOn, String notes) {
        jdbcTemplate.update(
                "UPDATE medical_history SET condition_name = ?, diagnosed_on = ?, notes = ? WHERE id = ?",
                condition, diagnosedOn != null ? Date.valueOf(diagnosedOn) : null, notes, id
        );
    }

    public void deleteHistory(Long id) {
        jdbcTemplate.update("DELETE FROM medical_history WHERE id = ?", id);
    }

    public void deleteHistoryForPatient(Long patientId) {
        jdbcTemplate.update("DELETE FROM medical_history WHERE patient_id = ?", patientId);
    }

    // ---------------------------------------------------------------------
    // Allergies
    // ---------------------------------------------------------------------

    private static final RowMapper<Allergy> ALLERGY_ROW_MAPPER = (rs, rowNum) -> {
        Allergy allergy = new Allergy();
        allergy.setId(rs.getLong("id"));
        allergy.setPatientId(rs.getLong("patient_id"));
        allergy.setAllergen(rs.getString("allergen"));
        allergy.setSeverity(AllergySeverity.valueOf(rs.getString("severity")));
        allergy.setReaction(rs.getString("reaction"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            allergy.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            allergy.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return allergy;
    };

    public Optional<Allergy> findAllergyById(Long id) {
        List<Allergy> results = jdbcTemplate.query(
                "SELECT id, patient_id, allergen, severity, reaction, created_at, updated_at " +
                        "FROM allergies WHERE id = ?",
                ALLERGY_ROW_MAPPER, id
        );
        return results.stream().findFirst();
    }

    /** Every allergy for one patient, most severe first, then most recently added. */
    public List<Allergy> findAllergiesByPatient(Long patientId) {
        return jdbcTemplate.query(
                "SELECT id, patient_id, allergen, severity, reaction, created_at, updated_at " +
                        "FROM allergies WHERE patient_id = ? " +
                        "ORDER BY FIELD(severity, 'SEVERE', 'MODERATE', 'MILD'), created_at DESC",
                ALLERGY_ROW_MAPPER, patientId
        );
    }

    public Allergy insertAllergy(Allergy allergy) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO allergies (patient_id, allergen, severity, reaction, created_at, updated_at) " +
                            "VALUES (?, ?, ?, ?, NOW(), NOW())",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, allergy.getPatientId());
            ps.setString(2, allergy.getAllergen());
            ps.setString(3, allergy.getSeverity().name());
            ps.setString(4, allergy.getReaction());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        allergy.setId(generatedId != null ? generatedId.longValue() : null);
        return allergy;
    }

    public void updateAllergy(Long id, String allergen, AllergySeverity severity, String reaction) {
        jdbcTemplate.update(
                "UPDATE allergies SET allergen = ?, severity = ?, reaction = ? WHERE id = ?",
                allergen, severity.name(), reaction, id
        );
    }

    public void deleteAllergy(Long id) {
        jdbcTemplate.update("DELETE FROM allergies WHERE id = ?", id);
    }

    public void deleteAllergiesForPatient(Long patientId) {
        jdbcTemplate.update("DELETE FROM allergies WHERE patient_id = ?", patientId);
    }
}
