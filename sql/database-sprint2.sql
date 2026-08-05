-- ============================================================
-- SmartCare Healthcare Platform — Sprint 2 Database
-- Appointment & Service Booking Module
-- Member: Rony Miah (24141084)
-- ============================================================

CREATE DATABASE IF NOT EXISTS healthcare_platform
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE healthcare_platform;

-- ============================================================
-- USERS (required by appointments FK)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(120)  NOT NULL,
    email         VARCHAR(160)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(40)   NOT NULL,
    phone         VARCHAR(40),
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_role (role),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- APPOINTMENTS (Sprint 2 feature)
-- ============================================================
CREATE TABLE IF NOT EXISTS appointments (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id   BIGINT NOT NULL,
    doctor_id    BIGINT NOT NULL,
    scheduled_at DATETIME,
    status       VARCHAR(40) DEFAULT 'pending',
    reason       TEXT,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Users (password for all: password123)
INSERT INTO users (full_name, email, password_hash, role, phone, is_active) VALUES
('Admin User',       'admin@health.test',    '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'ADMIN',     '+8801700000000', TRUE),
('Nadia Rahman',     'patient@health.test',  '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'PATIENT',   '+8801700000001', TRUE),
('Dr. Arif Khan',    'doctor@health.test',   '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'DOCTOR',    '+8801700000002', TRUE),
('City Care Hospital',  'hospital@health.test',  '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'HOSPITAL',  '+8801700000003', TRUE),
('MediQuick Pharmacy',  'pharmacy@health.test',  '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'PHARMACY',  '+8801700000004', TRUE);

-- Appointments (Sprint 2)
INSERT INTO appointments (patient_id, doctor_id, scheduled_at, status, reason) VALUES
(2, 3, DATE_ADD(NOW(), INTERVAL 1 HOUR),  'pending',   'Chest pain follow-up'),
(2, 3, DATE_ADD(NOW(), INTERVAL 3 HOUR),  'confirmed', 'Routine checkup'),
(2, 3, DATE_ADD(NOW(), INTERVAL 1 DAY),   'pending',   'Blood pressure review');
