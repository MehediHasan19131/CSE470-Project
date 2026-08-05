-- Reference copy - identical to src/main/resources/schema.sql, which is the one
-- Spring Boot executes on startup when spring.sql.init.mode=always.
-- Kept here too so the schema is easy to find without digging through src/.
--
-- Column types mirror the JPA entities in com.healthcare.platform.model
-- (Long ids -> BIGINT, role stored as a string via @Enumerated(EnumType.STRING)).

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(120) NOT NULL,
    email         VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(40)  NOT NULL,
    phone         VARCHAR(40),
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX ix_users_email (email),
    INDEX ix_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS profiles (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE,
    address             VARCHAR(255),
    city                VARCHAR(100),
    bio                 TEXT,
    specialization      VARCHAR(120),
    license_number      VARCHAR(120),
    service_name        VARCHAR(160),
    emergency_available BOOLEAN NOT NULL DEFAULT FALSE,
    latitude            DOUBLE,
    longitude           DOUBLE,
    -- Doctor & Patient Module (Sprint 1 - Imtiaz Zaman Sami): nullable extra
    -- fields so a profile row can carry doctor or patient specific details.
    qualification       VARCHAR(150),
    experience_years    INT,
    consultation_fee    DOUBLE,
    date_of_birth       DATE,
    gender              VARCHAR(20),
    blood_group         VARCHAR(5),
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ratings (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_user_id   BIGINT NOT NULL,
    reviewer_user_id BIGINT,
    score            INT    NOT NULL,
    comment          TEXT,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX ix_ratings_target_user_id (target_user_id),
    CONSTRAINT fk_ratings_target   FOREIGN KEY (target_user_id)   REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ratings_reviewer FOREIGN KEY (reviewer_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS appointments (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id   BIGINT NOT NULL,
    doctor_id    BIGINT NOT NULL,
    scheduled_at DATETIME NOT NULL,
    status       VARCHAR(40) NOT NULL DEFAULT 'pending',
    reason       TEXT,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX ix_appointments_patient_id (patient_id),
    INDEX ix_appointments_doctor_id (doctor_id),
    INDEX ix_appointments_scheduled_at (scheduled_at),
    CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointments_doctor  FOREIGN KEY (doctor_id)  REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS medicines (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(120)  NOT NULL,
    description    TEXT,
    price          DECIMAL(10,2) NOT NULL,
    stock_quantity INT           NOT NULL,
    active         BOOLEAN       NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS app_settings (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `key`      VARCHAR(80) NOT NULL UNIQUE,
    `value`    TEXT        NOT NULL,
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX ix_app_settings_key (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Pharmacy Service Module (Sprint 2 - Imtiaz Zaman Sami).
-- Ids are BIGINT to match the Long primary keys on the JPA entities; the
-- medicines table is declared once, above, alongside the other Sprint 1 tables.
CREATE TABLE IF NOT EXISTS orders (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id       BIGINT        NOT NULL,
    status           VARCHAR(30)   NOT NULL DEFAULT 'PENDING',
    total_amount     DECIMAL(10,2) NOT NULL DEFAULT 0,
    delivery_address VARCHAR(255),
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX ix_orders_patient_id (patient_id),
    CONSTRAINT fk_orders_patient FOREIGN KEY (patient_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS order_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT        NOT NULL,
    medicine_id BIGINT        NOT NULL,
    quantity    INT           NOT NULL,
    unit_price  DECIMAL(10,2) NOT NULL,
    INDEX ix_order_items_order_id (order_id),
    CONSTRAINT fk_order_items_order    FOREIGN KEY (order_id)    REFERENCES orders(id),
    CONSTRAINT fk_order_items_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
