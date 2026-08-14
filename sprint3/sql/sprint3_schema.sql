-- Sprint 3 additions — Telemedicine (Member 3: Mehedi Hasan, 23301352).
-- Run this AFTER sql/schema.sql (Sprint 1) and sprint2/sql/sprint2_schema.sql (Sprint 2).
-- Hibernate will also auto-create this table on startup because
-- spring.jpa.hibernate.ddl-auto=update is already set in application.properties.
-- This file is provided for the ERD / report and for manual setups.

CREATE TABLE IF NOT EXISTS consultations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  appointment_id INT NOT NULL,
  patient_id INT NOT NULL,
  doctor_id INT NOT NULL,
  room_name VARCHAR(120) NOT NULL UNIQUE,
  status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
  notes TEXT,
  prescription TEXT,
  scheduled_at DATETIME NOT NULL,
  started_at DATETIME,
  ended_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_consultations_appointment_id (appointment_id),
  INDEX ix_consultations_patient_id (patient_id),
  INDEX ix_consultations_doctor_id (doctor_id),
  INDEX ix_consultations_status (status),
  CONSTRAINT fk_consultations_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id),
  CONSTRAINT fk_consultations_patient FOREIGN KEY (patient_id) REFERENCES users(id),
  CONSTRAINT fk_consultations_doctor FOREIGN KEY (doctor_id) REFERENCES users(id)
);
