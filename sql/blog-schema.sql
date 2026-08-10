-- Reference copy - identical to src/main/resources/blog-schema.sql, which is
-- the one Spring Boot actually executes automatically on startup. Kept here
-- too so the schema is easy to find without digging through src/.
--
-- posts + comments tables for Health Blog & Community (Sprint 4).
-- Executed automatically by Spring Boot on startup, right after
-- health-schema.sql (see spring.sql.init.schema-locations in
-- application.properties) - plain SQL, no Hibernate/JPA, same "no ORM" rule
-- as the rest of the project.
--
-- Design:
--   posts    - one row per published article/post. `author_id` references
--              `users` - any authenticated role can publish (see README for
--              why this sprint doesn't restrict authorship to a subset of
--              roles the way Sprint 3 restricted the health profile to
--              patients).
--   comments - one row per comment on a post. References both `posts`
--              (which post it's on) and `users` (who wrote it). ON DELETE
--              CASCADE on post_id so deleting a post cleans up its comments
--              automatically at the database level - BlogJdbcRepository's
--              deletePost(...) also deletes them explicitly first, since
--              relying on cascade alone would silently vary by which MySQL
--              storage engine ends up in use.
--
-- Both tables reference `users` (created by schema.sql, which runs first).

CREATE TABLE IF NOT EXISTS posts (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id  BIGINT        NOT NULL,
    title      VARCHAR(200)  NOT NULL,
    content    TEXT          NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT        NOT NULL,
    author_id  BIGINT        NOT NULL,
    content    VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users (id)
);
