package com.healthcare.platform.auth;

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
 * Registration + profile data access, written as plain JDBC (no Spring Data JPA,
 * no @Entity, no Hibernate) - every query below is hand-written SQL, and every row
 * is mapped to {@link AuthUser} by hand. This satisfies the course's "no ORM" rule
 * for the auth module even though the rest of the app uses JPA.
 */
@Repository
public class AuthUserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<AuthUser> ROW_MAPPER = new RowMapper<AuthUser>() {
        @Override
        public AuthUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            AuthUser user = new AuthUser();
            user.setId(rs.getLong("id"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setRole(UserRole.valueOf(rs.getString("role")));
            user.setPhone(rs.getString("phone"));
            user.setActive(rs.getBoolean("is_active"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                user.setCreatedAt(createdAt.toLocalDateTime());
            }
            return user;
        }
    };

    private static final String SELECT_COLUMNS =
            "SELECT id, full_name, email, password_hash, role, phone, is_active, created_at FROM users ";

    public AuthUserJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class,
                email
        );
        return count != null && count > 0;
    }

    public Optional<AuthUser> findByEmail(String email) {
        List<AuthUser> results = jdbcTemplate.query(
                SELECT_COLUMNS + "WHERE email = ?",
                ROW_MAPPER,
                email
        );
        return results.stream().findFirst();
    }

    public Optional<AuthUser> findById(Long id) {
        List<AuthUser> results = jdbcTemplate.query(
                SELECT_COLUMNS + "WHERE id = ?",
                ROW_MAPPER,
                id
        );
        return results.stream().findFirst();
    }

    /** Inserts a new row and returns the same object with its generated id set. */
    public AuthUser insert(AuthUser user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (full_name, email, password_hash, role, phone, is_active, created_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?, NOW())",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getPhone());
            ps.setBoolean(6, true);
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        user.setId(generatedId != null ? generatedId.longValue() : null);
        user.setActive(true);
        return user;
    }

    public void updateProfile(Long id, String fullName, String phone) {
        jdbcTemplate.update(
                "UPDATE users SET full_name = ?, phone = ? WHERE id = ?",
                fullName, phone, id
        );
    }
}
