package com.healthcare.platform.review;

import com.healthcare.platform.model.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Review + rating data access, written as plain JDBC (no Spring Data JPA, no
 * @Entity, no Hibernate) - every query below is hand-written SQL, and every
 * row is mapped by hand. Same "no ORM" rule Member 1 used for
 * {@code AuthUserJdbcRepository}, applied to this sprint's `reviews` and
 * `rating_summaries` tables.
 */
@Repository
public class ReviewJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReviewJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Always joins `users` for the reviewer's name - cheap, and every caller
    // that reads a Review wants it (for display), except the plain
    // duplicate-review check below which doesn't care either way.
    private static final String SELECT_COLUMNS =
            "SELECT r.id, r.reviewer_id, u.full_name AS reviewer_name, r.target_id, " +
                    "r.rating, r.comment, r.created_at, r.updated_at " +
                    "FROM reviews r JOIN users u ON u.id = r.reviewer_id ";

    private static final RowMapper<Review> ROW_MAPPER = new RowMapper<Review>() {
        @Override
        public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
            Review review = new Review();
            review.setId(rs.getLong("id"));
            review.setReviewerId(rs.getLong("reviewer_id"));
            review.setReviewerName(rs.getString("reviewer_name"));
            review.setTargetId(rs.getLong("target_id"));
            review.setRating(rs.getInt("rating"));
            review.setComment(rs.getString("comment"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                review.setCreatedAt(createdAt.toLocalDateTime());
            }
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                review.setUpdatedAt(updatedAt.toLocalDateTime());
            }
            return review;
        }
    };

    public Optional<Review> findById(Long id) {
        List<Review> results = jdbcTemplate.query(SELECT_COLUMNS + "WHERE r.id = ?", ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    public Optional<Review> findByReviewerAndTarget(Long reviewerId, Long targetId) {
        List<Review> results = jdbcTemplate.query(
                SELECT_COLUMNS + "WHERE r.reviewer_id = ? AND r.target_id = ?",
                ROW_MAPPER, reviewerId, targetId
        );
        return results.stream().findFirst();
    }

    public List<Review> findByTarget(Long targetId) {
        return jdbcTemplate.query(
                SELECT_COLUMNS + "WHERE r.target_id = ? ORDER BY r.created_at DESC",
                ROW_MAPPER, targetId
        );
    }

    /**
     * Reviews a given user has written, most recent first - joins BOTH the
     * reviewer's name (for consistency with the other queries) and the
     * target's name (so "my reviews" can show who each review is about).
     * Used by the patient's dashboard panel on {@code /profile}.
     */
    public List<Review> findByReviewer(Long reviewerId) {
        return jdbcTemplate.query(
                "SELECT r.id, r.reviewer_id, ru.full_name AS reviewer_name, r.target_id, tu.full_name AS target_name, " +
                        "r.rating, r.comment, r.created_at, r.updated_at " +
                        "FROM reviews r " +
                        "JOIN users ru ON ru.id = r.reviewer_id " +
                        "JOIN users tu ON tu.id = r.target_id " +
                        "WHERE r.reviewer_id = ? ORDER BY r.created_at DESC",
                (rs, rowNum) -> {
                    Review review = new Review();
                    review.setId(rs.getLong("id"));
                    review.setReviewerId(rs.getLong("reviewer_id"));
                    review.setReviewerName(rs.getString("reviewer_name"));
                    review.setTargetId(rs.getLong("target_id"));
                    review.setTargetName(rs.getString("target_name"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        review.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        review.setUpdatedAt(updatedAt.toLocalDateTime());
                    }
                    return review;
                },
                reviewerId
        );
    }

    /** Every target this reviewer has reviewed - used to know whose rating summary needs recomputing after a bulk delete. */
    public List<Long> findDistinctTargetsReviewedBy(Long reviewerId) {
        return jdbcTemplate.queryForList("SELECT DISTINCT target_id FROM reviews WHERE reviewer_id = ?", Long.class, reviewerId);
    }

    /**
     * Deletes every review a user is involved in, either as reviewer or as
     * target. Only called from {@code AdminUserService.deleteUser(...)}
     * before deleting the user row itself - `reviews`/`rating_summaries` both have a
     * foreign key on `users.id`, so this is required to avoid a constraint
     * violation when an admin deletes an account.
     */
    public void deleteReviewsInvolvingUser(Long userId) {
        jdbcTemplate.update("DELETE FROM reviews WHERE reviewer_id = ? OR target_id = ?", userId, userId);
    }

    /** Removes a target's aggregate row entirely - used when that target (a provider) is itself being deleted. */
    public void deleteRatingSummary(Long targetId) {
        jdbcTemplate.update("DELETE FROM rating_summaries WHERE target_id = ?", targetId);
    }

    /** Inserts a new row and returns the same object with its generated id set. */
    public Review insert(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO reviews (reviewer_id, target_id, rating, comment, created_at, updated_at) " +
                            "VALUES (?, ?, ?, ?, NOW(), NOW())",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, review.getReviewerId());
            ps.setLong(2, review.getTargetId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        review.setId(generatedId != null ? generatedId.longValue() : null);
        return review;
    }

    public void update(Long id, int rating, String comment) {
        jdbcTemplate.update(
                "UPDATE reviews SET rating = ?, comment = ? WHERE id = ?",
                rating, comment, id
        );
    }

    public Optional<RatingSummary> findRatingSummary(Long targetId) {
        List<RatingSummary> results = jdbcTemplate.query(
                "SELECT target_id, average_rating, total_reviews, updated_at FROM rating_summaries WHERE target_id = ?",
                (rs, rowNum) -> {
                    RatingSummary summary = new RatingSummary();
                    summary.setTargetId(rs.getLong("target_id"));
                    summary.setAverageRating(rs.getDouble("average_rating"));
                    summary.setTotalReviews(rs.getInt("total_reviews"));
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        summary.setUpdatedAt(updatedAt.toLocalDateTime());
                    }
                    return summary;
                },
                targetId
        );
        return results.stream().findFirst();
    }

    /**
     * Recomputes the average rating + review count for a target directly from
     * `reviews`, and upserts the result into `rating_summaries`. Called after every
     * insert/update so `rating_summaries` never drifts out of sync - this is what
     * powers the "Rating Display" frontend piece without it having to
     * aggregate the whole reviews table on every page load.
     */
    public void refreshRatingSummary(Long targetId) {
        jdbcTemplate.update(
                "INSERT INTO rating_summaries (target_id, average_rating, total_reviews) " +
                        "SELECT ?, COALESCE(AVG(rating), 0), COUNT(*) FROM reviews WHERE target_id = ? " +
                        "ON DUPLICATE KEY UPDATE " +
                        "average_rating = VALUES(average_rating), total_reviews = VALUES(total_reviews)",
                targetId, targetId
        );
    }

    /**
     * Reviewable providers (every non-admin, non-patient user) plus their
     * current rating, for the {@code GET /reviews} directory page. Read-only
     * join against `users` - doesn't touch or duplicate AuthUserJdbcRepository.
     */
    public List<ProviderSummary> findProviders() {
        return jdbcTemplate.query(
                "SELECT u.id, u.full_name, u.role, " +
                        "COALESCE(r.average_rating, 0) AS average_rating, " +
                        "COALESCE(r.total_reviews, 0) AS total_reviews " +
                        "FROM users u LEFT JOIN rating_summaries r ON r.target_id = u.id " +
                        "WHERE u.role NOT IN ('ADMIN', 'PATIENT') AND u.is_active = TRUE " +
                        "ORDER BY u.full_name",
                (rs, rowNum) -> {
                    ProviderSummary provider = new ProviderSummary();
                    provider.setId(rs.getLong("id"));
                    provider.setFullName(rs.getString("full_name"));
                    provider.setRole(UserRole.valueOf(rs.getString("role")));
                    provider.setAverageRating(rs.getDouble("average_rating"));
                    provider.setTotalReviews(rs.getInt("total_reviews"));
                    return provider;
                }
        );
    }
}
