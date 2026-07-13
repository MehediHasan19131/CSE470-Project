-- ============================================================
-- SmartCare Healthcare Platform — Schema Only
-- Sprint 1 (Rony Miah - 24141084)
-- ============================================================

CREATE DATABASE IF NOT EXISTS healthcare_platform
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE healthcare_platform;

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
    name           VARCHAR(120)  NOT NULL,
    description    TEXT,
    price          DECIMAL(10,2) NOT NULL,
    stock_quantity INT           NOT NULL,
    active         BOOLEAN       DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS app_settings (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `key`      VARCHAR(80) NOT NULL UNIQUE,
    `value`    TEXT        NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
