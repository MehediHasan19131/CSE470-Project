-- Sprint 2 additions.
-- Run this AFTER sql/schema.sql (Sprint 1). Safe to re-run (CREATE TABLE IF NOT EXISTS).
-- If you run the Spring Boot app with spring.jpa.hibernate.ddl-auto=update (see application.properties),
-- Hibernate will create these tables automatically from the new entity classes and you do not need to
-- run this file by hand. It is provided for manual/reference setup and for the SQL diagram in your report.

CREATE TABLE IF NOT EXISTS ambulances (
  id INT AUTO_INCREMENT PRIMARY KEY,
  provider_id INT NOT NULL,
  vehicle_number VARCHAR(40) NOT NULL,
  vehicle_type VARCHAR(40) NOT NULL DEFAULT 'BASIC',
  capacity INT NOT NULL DEFAULT 1,
  driver_name VARCHAR(120),
  driver_phone VARCHAR(40),
  is_available BOOLEAN NOT NULL DEFAULT TRUE,
  latitude DOUBLE,
  longitude DOUBLE,
  base_fare DECIMAL(10,2) NOT NULL DEFAULT 300.00,
  per_km_rate DECIMAL(10,2) NOT NULL DEFAULT 40.00,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX ix_ambulances_provider_id (provider_id),
  INDEX ix_ambulances_is_available (is_available),
  CONSTRAINT fk_ambulances_provider FOREIGN KEY (provider_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS ambulance_requests (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  ambulance_id INT,
  pickup_address VARCHAR(255) NOT NULL,
  pickup_latitude DOUBLE NOT NULL,
  pickup_longitude DOUBLE NOT NULL,
  drop_address VARCHAR(255),
  drop_latitude DOUBLE,
  drop_longitude DOUBLE,
  emergency_type VARCHAR(60),
  notes TEXT,
  status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
  fare_estimate DECIMAL(10,2),
  requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX ix_ambulance_requests_patient_id (patient_id),
  INDEX ix_ambulance_requests_ambulance_id (ambulance_id),
  INDEX ix_ambulance_requests_status (status),
  CONSTRAINT fk_ambulance_requests_patient FOREIGN KEY (patient_id) REFERENCES users(id),
  CONSTRAINT fk_ambulance_requests_ambulance FOREIGN KEY (ambulance_id) REFERENCES ambulances(id)
);

CREATE TABLE IF NOT EXISTS medicine_orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  pharmacy_id INT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  delivery_address VARCHAR(255),
  total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX ix_medicine_orders_patient_id (patient_id),
  INDEX ix_medicine_orders_pharmacy_id (pharmacy_id),
  CONSTRAINT fk_medicine_orders_patient FOREIGN KEY (patient_id) REFERENCES users(id),
  CONSTRAINT fk_medicine_orders_pharmacy FOREIGN KEY (pharmacy_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS medicine_order_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  medicine_id INT NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  INDEX ix_medicine_order_items_order_id (order_id),
  CONSTRAINT fk_medicine_order_items_order FOREIGN KEY (order_id) REFERENCES medicine_orders(id),
  CONSTRAINT fk_medicine_order_items_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id)
);
