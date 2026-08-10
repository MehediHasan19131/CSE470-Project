-- Reference copy - identical to src/main/resources/schema.sql, which is the one
-- Spring Boot actually executes automatically on startup (spring.sql.init.mode=always).
-- Kept here too so the schema is easy to find without digging through src/.
--
-- users table for Member 1 (Authentication & User Management).
-- Plain SQL, no Hibernate/JPA anywhere in this submission.

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(120) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL,
    phone         VARCHAR(30),
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
