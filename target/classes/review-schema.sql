-- reviews + ratings tables for Member 2 (Review & Rating System).
-- Executed automatically by Spring Boot on startup, right after schema.sql
-- (see spring.sql.init.schema-locations in application.properties) - plain SQL,
-- no Hibernate/JPA, same "no ORM" rule as the rest of the project.
--
-- Design:
--   reviews  - one row per (reviewer, target) pair: the star rating + written
--              comment a user leaves for a provider (doctor/hospital/pharmacy/
--              diagnostic centre/ambulance service). A reviewer can only have
--              ONE review per target (enforced by the unique key below) - to
--              change their opinion they update that row, they don't insert a
--              second one. That's why the task has both "Create Review" and
--              "Update Review" as separate backend items.
--   ratings  - one row per target: the running average + count, recomputed
--              from `reviews` every time a review is created or updated. Kept
--              as its own table (rather than computed on the fly on every page
--              load) so the "Rating Display" frontend piece has a cheap,
--              single-row lookup instead of aggregating the whole reviews
--              table on every request.
--
-- Both tables reference `users` (created by schema.sql, which runs first) -
-- reviewers AND targets are just rows in that same table, distinguished by
-- role.

CREATE TABLE IF NOT EXISTS reviews (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id   BIGINT        NOT NULL,
    target_id     BIGINT        NOT NULL,
    rating        TINYINT       NOT NULL,
    comment       VARCHAR(1000),
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id),
    CONSTRAINT fk_reviews_target   FOREIGN KEY (target_id)   REFERENCES users (id),
    CONSTRAINT uq_reviews_reviewer_target UNIQUE (reviewer_id, target_id),
    CONSTRAINT chk_reviews_rating_range CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE IF NOT EXISTS ratings (
    target_id       BIGINT        PRIMARY KEY,
    average_rating  DECIMAL(3,2)  NOT NULL DEFAULT 0.00,
    total_reviews   INT           NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ratings_target FOREIGN KEY (target_id) REFERENCES users (id)
);
