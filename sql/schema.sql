-- Reference copy - identical to src/main/resources/schema.sql, which is the one
-- Spring Boot actually executes automatically on startup (spring.sql.init.mode=always).
-- Kept here too so the schema is easy to find without digging through src/.
--
-- users table for Member 1 (Authentication & User Management).
-- Plain SQL, no Hibernate/JPA anywhere in this submission.

CREATE TABLE IF NOT EXISTS users (
<<<<<<< HEAD
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(120) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL,
    phone         VARCHAR(30),
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
=======
  id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('ADMIN','PATIENT','DOCTOR','HOSPITAL','PHARMACY','DIAGNOSTIC','AMBULANCE') NOT NULL,
  phone VARCHAR(40),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX ix_users_email (email),
  INDEX ix_users_role (role)
);

CREATE TABLE IF NOT EXISTS profiles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL UNIQUE,
  address VARCHAR(255),
  city VARCHAR(80),
  bio TEXT,
  specialization VARCHAR(120),
  license_number VARCHAR(120),
  service_name VARCHAR(160),
  emergency_available BOOLEAN NOT NULL DEFAULT FALSE,
  latitude DOUBLE,
  longitude DOUBLE,
  -- Doctor & Patient Module (Sprint 1 - Imtiaz Zaman Sami): nullable extra
  -- fields so a Profile row can carry doctor or patient specific details.
  qualification VARCHAR(150),
  experience_years INT,
  consultation_fee DOUBLE,
  date_of_birth DATE,
  gender VARCHAR(20),
  blood_group VARCHAR(5),
  CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS ratings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  target_user_id INT NOT NULL,
  reviewer_user_id INT,
  score INT NOT NULL,
  comment TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX ix_ratings_target_user_id (target_user_id),
  CONSTRAINT fk_ratings_target FOREIGN KEY (target_user_id) REFERENCES users(id),
  CONSTRAINT fk_ratings_reviewer FOREIGN KEY (reviewer_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS appointments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  doctor_id INT NOT NULL,
  scheduled_at DATETIME NOT NULL,
  status VARCHAR(40) NOT NULL DEFAULT 'pending',
  reason TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX ix_appointments_patient_id (patient_id),
  INDEX ix_appointments_doctor_id (doctor_id),
  INDEX ix_appointments_scheduled_at (scheduled_at),
  CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES users(id),
  CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS app_settings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  `key` VARCHAR(80) NOT NULL UNIQUE,
  value TEXT NOT NULL,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX ix_app_settings_key (`key`)
>>>>>>> origin/sami-sprint1
);
