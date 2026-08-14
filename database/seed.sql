-- ============================================================
-- SmartCare Healthcare Platform
-- Database: healthcare_platform
-- Schema + Seed Data for Sprint 1 (Rony Miah - 24141084)
-- ============================================================

CREATE DATABASE IF NOT EXISTS healthcare_platform
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE healthcare_platform;

-- ============================================================
-- TABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(120)  NOT NULL,
    email       VARCHAR(160)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role        VARCHAR(40)   NOT NULL,
    phone       VARCHAR(40),
    is_active   BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_role (role),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS profiles (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT       NOT NULL UNIQUE,
    address             VARCHAR(255),
    city                VARCHAR(100),
    bio                 TEXT,
    specialization      VARCHAR(100),
    license_number      VARCHAR(60),
    service_name        VARCHAR(100),
    emergency_available BOOLEAN      DEFAULT FALSE,
    latitude            DOUBLE,
    longitude           DOUBLE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ratings (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_user_id    BIGINT NOT NULL,
    reviewer_user_id  BIGINT,
    score             INT    NOT NULL,
    comment           TEXT,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (target_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

CREATE TABLE IF NOT EXISTS medicines (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(120) NOT NULL,
    description    TEXT,
    price          DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL,
    active         BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS app_settings (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `key`      VARCHAR(80) NOT NULL UNIQUE,
    `value`    TEXT NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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
('MediQuick Pharmacy',  'pharmacy@health.test',  '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'PHARMACY',  '+8801700000004', TRUE),
('Prime Diagnostic Centre', 'diagnostic@health.test', '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'DIAGNOSTIC','+8801700000005', TRUE),
('Rapid Ambulance',  'ambulance@health.test', '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'AMBULANCE', '+8801700000006', TRUE);

-- Profiles
INSERT INTO profiles (user_id, address, city, bio, specialization, license_number, service_name, emergency_available, latitude, longitude) VALUES
(1, 'Road 1, Dhaka', 'Dhaka', 'Platform admin profile.', NULL, NULL, 'Platform Admin', FALSE, 23.8103, 90.4125),
(2, 'Road 2, Dhaka', 'Dhaka', 'Patient profile.', NULL, NULL, 'Patient', FALSE, 23.8203, 90.4225),
(3, 'Road 3, Dhaka', 'Dhaka', 'Cardiology specialist profile.', 'Cardiology', 'DOC-1001', NULL, FALSE, 23.8303, 90.4325),
(4, 'Road 4, Dhaka', 'Dhaka', 'Multi-speciality hospital.', NULL, 'HOSP-1001', 'Multi-speciality Hospital', TRUE, 23.8403, 90.4425),
(5, 'Road 5, Dhaka', 'Dhaka', '24/7 pharmacy service.', NULL, 'PHAR-1001', '24/7 Pharmacy', TRUE, 23.8503, 90.4525),
(6, 'Road 6, Dhaka', 'Dhaka', 'Diagnostic centre service.', NULL, 'DIAG-1001', 'Diagnostics', FALSE, 23.8603, 90.4625),
(7, 'Road 7, Dhaka', 'Dhaka', 'Emergency ambulance service.', NULL, 'AMB-1001', 'Emergency Ambulance', TRUE, 23.8703, 90.4725);

-- Ratings
INSERT INTO ratings (target_user_id, reviewer_user_id, score, comment) VALUES
(3, 2, 5, 'Good service'),
(4, 2, 5, 'Good service'),
(5, 2, 5, 'Good service'),
(7, 2, 5, 'Good service');

-- Appointments
INSERT INTO appointments (patient_id, doctor_id, scheduled_at, status, reason) VALUES
(2, 3, DATE_ADD(NOW(), INTERVAL 1 HOUR),  'pending',   'Chest pain follow-up'),
(2, 3, DATE_ADD(NOW(), INTERVAL 3 HOUR),  'confirmed', 'Routine checkup'),
(2, 3, DATE_ADD(NOW(), INTERVAL 1 DAY),   'pending',   'Blood pressure review');

-- Medicines
INSERT INTO medicines (name, description, price, stock_quantity) VALUES
('Paracetamol 500mg',  'Pain relief and fever reducer',   2.50,  120),
('Amoxicillin 250mg',  'Antibiotic capsules',             8.00,  45),
('Vitamin C 1000mg',   'Immune support tablets',          5.50,  8),
('Cough Syrup',        'Dry cough relief',                6.75,  30);

-- App Settings
INSERT INTO app_settings (`key`, `value`) VALUES
('appointment_reminders', 'enabled'),
('ai_agent_status',       'planned'),
('payment_gateway',       'sandbox');
