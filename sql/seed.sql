-- Reference copy - identical to src/main/resources/data.sql, which is the one
-- Spring Boot actually executes automatically on startup.
--
-- Demo accounts (password123 for all - already BCrypt-hashed below).

INSERT IGNORE INTO users (full_name, email, password_hash, role, phone, is_active) VALUES
    ('Admin User',            'admin@health.test',      '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'ADMIN',      '+8801700000000', TRUE),
    ('Nadia Rahman',          'patient@health.test',    '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'PATIENT',    '+8801700000001', TRUE),
    ('Dr. Arif Khan',         'doctor@health.test',     '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'DOCTOR',     '+8801700000002', TRUE),
    ('City Care Hospital',    'hospital@health.test',   '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'HOSPITAL',   '+8801700000003', TRUE),
    ('MediQuick Pharmacy',    'pharmacy@health.test',   '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'PHARMACY',   '+8801700000004', TRUE),
    ('Prime Diagnostic Centre','diagnostic@health.test','$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'DIAGNOSTIC', '+8801700000005', TRUE),
    ('Rapid Ambulance',       'ambulance@health.test',  '$2b$10$YmiVI7ApTZkCrD5P.Nr6cuyoSQdi8MDPjtI9bhUVMkP79pVxJwRka', 'AMBULANCE',  '+8801700000006', TRUE);
