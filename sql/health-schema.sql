-- Reference copy - identical to src/main/resources/health-schema.sql, which is
-- the one Spring Boot actually executes automatically on startup. Kept here
-- too so the schema is easy to find without digging through src/.
--
-- medical_history + allergies tables for Member 1 (Health Profile, Sprint 3).
-- Executed automatically by Spring Boot on startup, right after schema.sql
-- and review-schema.sql (see spring.sql.init.schema-locations in
-- application.properties) - plain SQL, no Hibernate/JPA, same "no ORM" rule
-- as the rest of the project.
--
-- Design:
--   medical_history - one row per condition/diagnosis/procedure a patient
--                      records about themselves (e.g. "Type 2 Diabetes",
--                      "Appendectomy"). A patient can have any number of
--                      these - unlike `reviews`, there's no uniqueness
--                      constraint, since a health history is naturally a
--                      growing list, not a one-per-relationship record.
--                      This is what "Add History" and "Update History"
--                      (the two assigned backend items) read and write.
--   allergies        - one row per allergen a patient has recorded, with a
--                       severity (MILD/MODERATE/SEVERE) and optional reaction
--                       notes. Kept as its own table rather than folded into
--                       medical_history because it has its own shape
--                       (severity, reaction) and is displayed as its own
--                       panel on the Health Profile Page - the same reason
--                       Sprint 2 kept `reviews` and `ratings` separate.
--
-- Both tables reference `users` (created by schema.sql, which runs first) -
-- a "patient" is just a row in that same table with role = 'PATIENT'.

CREATE TABLE IF NOT EXISTS medical_history (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id     BIGINT        NOT NULL,
    condition_name VARCHAR(150)  NOT NULL,
    diagnosed_on   DATE,
    notes          VARCHAR(1000),
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_medical_history_patient FOREIGN KEY (patient_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS allergies (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id     BIGINT        NOT NULL,
    allergen       VARCHAR(150)  NOT NULL,
    severity       VARCHAR(20)   NOT NULL DEFAULT 'MODERATE',
    reaction       VARCHAR(500),
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_allergies_patient FOREIGN KEY (patient_id) REFERENCES users (id),
    CONSTRAINT chk_allergies_severity CHECK (severity IN ('MILD', 'MODERATE', 'SEVERE'))
);
