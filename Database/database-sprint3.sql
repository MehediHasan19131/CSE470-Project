-- ============================================================
-- SmartCare Healthcare Platform — Sprint 3 Database
-- Medicine Reminder & Medicine History Module
-- Member: Rony Miah (24141084)
-- ============================================================

CREATE DATABASE IF NOT EXISTS healthcare_platform
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE healthcare_platform;

-- ============================================================
-- USERS (required by reminders FK)
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
-- MEDICINE REMINDERS (Sprint 3)
-- ============================================================
CREATE TABLE IF NOT EXISTS medicine_reminders (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id        BIGINT       NOT NULL,
    medicine_name     VARCHAR(120) NOT NULL,
    dosage            VARCHAR(80)  NOT NULL,
    frequency_per_day INT          NOT NULL,
    reminder_times    VARCHAR(200) NOT NULL,
    start_date        DATE,
    end_date          DATE,
    active            BOOLEAN      NOT NULL DEFAULT TRUE,
    notes             TEXT,
    created_at        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_reminder_patient_active (patient_id, active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- MEDICINE LOGS (history of taken/missed/skipped doses)
-- ============================================================
CREATE TABLE IF NOT EXISTS medicine_logs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    reminder_id    BIGINT      NOT NULL,
    scheduled_time VARCHAR(10),
    status         VARCHAR(20) NOT NULL,
    logged_at      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reminder_id) REFERENCES medicine_reminders(id) ON DELETE CASCADE,
    INDEX idx_log_reminder (reminder_id),
    INDEX idx_log_logged_at (logged_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Users (password for all: password123)
INSERT INTO users (full_name, email, password_hash, role, phone, is_active) VALUES
('Admin User',       'admin@health.test',    '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'ADMIN',     '+8801700000000', TRUE),
('Nadia Rahman',     'patient@health.test',  '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'PATIENT',   '+8801700000001', TRUE),
('Dr. Arif Khan',    'doctor@health.test',   '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'DOCTOR',    '+8801700000002', TRUE);

-- Medicine Reminders (3 reminders: 2 active, 1 inactive)
INSERT INTO medicine_reminders (patient_id, medicine_name, dosage, frequency_per_day, reminder_times, start_date, end_date, active, notes) VALUES
(2, 'Paracetamol', '500mg',    3, '08:00,14:00,20:00', DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 20 DAY), TRUE,  'Take after meals'),
(2, 'Vitamin C',   '1000mg',   1, '09:00',             DATE_SUB(CURDATE(), INTERVAL 5 DAY),  DATE_ADD(CURDATE(), INTERVAL 30 DAY), TRUE,  'Morning only'),
(2, 'Amoxicillin', '250mg',    2, '10:00,22:00',       DATE_SUB(CURDATE(), INTERVAL 3 DAY),  NULL,                                FALSE, NULL);

-- Medicine History logs (mix of TAKEN / MISSED / SKIPPED)
INSERT INTO medicine_logs (reminder_id, scheduled_time, status, logged_at) VALUES
(1, '08:00', 'TAKEN',   DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '14:00', 'TAKEN',   DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '20:00', 'MISSED',  DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, '09:00', 'TAKEN',   DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, '10:00', 'SKIPPED', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, '22:00', 'TAKEN',   DATE_SUB(NOW(), INTERVAL 2 DAY));
