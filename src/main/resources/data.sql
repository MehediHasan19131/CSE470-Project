-- Demo accounts (password123 for all - already BCrypt-hashed below).
-- Executed automatically by Spring Boot on startup, right after schema.sql.
-- INSERT IGNORE so re-running this on an existing database is harmless.

INSERT IGNORE INTO users (full_name, email, password_hash, role, phone, is_active) VALUES
    ('SmartCare Admin',            'admin@health.test',      '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'ADMIN',      '+8801700000000', TRUE),
    ('Nahian Mahmud',               'patient@health.test',    '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'PATIENT',    '+8801700000001', TRUE),
    ('Dr. Fabiha Fyroz Ursa',       'doctor@health.test',     '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'DOCTOR',     '+8801700000002', TRUE),
    ('Square Hospital',             'hospital@health.test',   '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'HOSPITAL',   '+8801700000003', TRUE),
    ('Lazz Pharma',                 'pharmacy@health.test',   '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'PHARMACY',   '+8801700000004', TRUE),
    ('Popular Diagnostic Centre',   'diagnostic@health.test', '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'DIAGNOSTIC', '+8801700000005', TRUE),
    ('Dhaka Emergency Ambulance Service', 'ambulance@health.test',  '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'AMBULANCE',  '+8801700000006', TRUE);
