CREATE TABLE IF NOT EXISTS users (
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
);

-- Pharmacy Service Module (Sprint 2 - Imtiaz Zaman Sami)
CREATE TABLE IF NOT EXISTS medicines (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  description TEXT,
  price DECIMAL(10, 2) NOT NULL,
  stock_quantity INT NOT NULL DEFAULT 0,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  INDEX ix_medicines_name (name)
);

CREATE TABLE IF NOT EXISTS orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
  delivery_address VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX ix_orders_patient_id (patient_id),
  CONSTRAINT fk_orders_patient FOREIGN KEY (patient_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  medicine_id INT NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10, 2) NOT NULL,
  INDEX ix_order_items_order_id (order_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_items_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);
