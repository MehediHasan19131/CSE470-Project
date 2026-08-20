-- ============================================================
-- SmartCare Healthcare Platform — Sprint 4 Database
-- Blood Donation & Medicine History Module
-- ============================================================

CREATE DATABASE IF NOT EXISTS healthcare_platform
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE healthcare_platform;

-- ============================================================
-- USERS (required by donors FK)
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
-- DONORS (Sprint 4 feature)
-- ============================================================
CREATE TABLE IF NOT EXISTS donors (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT,
    full_name         VARCHAR(100)  NOT NULL,
    blood_group       VARCHAR(5)    NOT NULL,
    phone             VARCHAR(20)   NOT NULL,
    city              VARCHAR(100),
    is_available      BOOLEAN       NOT NULL DEFAULT TRUE,
    last_donation_date DATE,
    created_at        DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_donor_blood_group (blood_group),
    INDEX idx_donor_city (city),
    INDEX idx_donor_available (is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BLOOD REQUESTS (Sprint 4 feature)
-- ============================================================
CREATE TABLE IF NOT EXISTS blood_requests (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_name    VARCHAR(100)  NOT NULL,
    requester_phone   VARCHAR(20)   NOT NULL,
    blood_group_needed VARCHAR(5)    NOT NULL,
    units_needed      INT           NOT NULL,
    hospital_or_location VARCHAR(200),
    urgency           VARCHAR(20)   NOT NULL,
    status            VARCHAR(20)   NOT NULL DEFAULT 'OPEN',
    needed_by_date    DATE,
    created_at        DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_request_blood_group_status (blood_group_needed, status),
    INDEX idx_request_needed_by (needed_by_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Users (password for all: password123)
INSERT INTO users (full_name, email, password_hash, role, phone, is_active) VALUES
('Admin User',       'admin@health.test',    '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'ADMIN',     '+8801700000000', TRUE),
('Rahim Ahmed',      'donor@health.test',    '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'AMBULANCE', '+8801700000001', TRUE),
('Dr. Sumona Aktar', 'doctor@health.test',   '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'DOCTOR',    '+8801700000002', TRUE),
('City Hospital',    'hospital@health.test', '$2a$04$xnWNXrATYXYaHFMKKOIQ7./UuURRuvlj4tQiifuXQLBZ5SmOsuATy', 'HOSPITAL',  '+8801700000003', TRUE);

-- Donors (3 donors with mixed blood groups)
INSERT INTO donors (full_name, blood_group, phone, city, is_available, last_donation_date) VALUES
('Rahim Ahmed', 'O+', '+8801712345678', 'Dhaka', TRUE, DATE_SUB(CURDATE(), INTERVAL 180 DAY)),
('Karim Hossain', 'A-', '+8801787654321', 'Chittagong', TRUE, DATE_SUB(CURDATE(), INTERVAL 400 DAY)),
('Fatima Begum', 'B+', '+8801755555555', 'Dhaka', FALSE, DATE_SUB(CURDATE(), INTERVAL 30 DAY));

-- Blood Requests (3 requests with OPEN/FULFILLED/EXPIRED status)
INSERT INTO blood_requests (requester_name, requester_phone, blood_group_needed, units_needed, hospital_or_location, urgency, status, needed_by_date) VALUES
('Dr. Sumona Aktar', '+8801722222222', 'A+', 2, 'City Hospital', 'HIGH', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 7 DAY)),
('City Hospital Blood Bank', '+8801733333333', 'O-', 3, 'City Hospital', 'CRITICAL', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
('Rahim Ahmed', '+8801711111111', 'B+', 1, 'Chittagong Medical College', 'MEDIUM', 'FULFILLED', DATE_SUB(CURDATE(), INTERVAL 5 DAY));