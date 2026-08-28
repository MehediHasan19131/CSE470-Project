package com.healthcare.platform.repository.blog;
import com.healthcare.platform.model.blog.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Health Blog & Community data access, written as plain JDBC (no Spring Data
 * JPA, no @Entity, no Hibernate) - every query below is hand-written SQL,
 * every row mapped by hand. Same "no ORM" rule the rest of the project
 * follows, applied to this sprint's `posts` and `comments` tables.
 * <p>
 * Every SELECT joins against `users` to fill in the author's name and role
 * (see {@code Post.authorName}/{@code Comment.authorName}) - the same trick
 * {@code ReviewJdbcRepository} uses for {@code reviewerName}, so a template
 * never has to look an id up itself.
 */
@Repository
public class BlogJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public BlogJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------------------------------------------------------------
    // Posts - "Create Post" (+ list/update/delete)
    // ---------------------------------------------------------------------

    private static final String POST_SELECT_COLUMNS =
            "SELECT p.id, p.author_id, u.full_name AS author_name, u.role AS author_role, " +
                    "p.title, p.content, p.created_at, p.updated_at, " +
                    "(SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS comment_count " +
                    "FROM posts p JOIN users u ON u.id = p.author_id ";

    private static final RowMapper<Post> POST_ROW_MAPPER = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setAuthorId(rs.getLong("author_id"));
        post.setAuthorName(rs.getString("author_name"));
        post.setAuthorRole(rs.getString("author_role"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setCommentCount(rs.getInt("comment_count"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            post.setCreatedAt(createdAt.toLocalDateTime());
        }
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            post.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return post;
    };

    public Optional<Post> findPostById(Long id) {
        List<Post> results = jdbcTemplate.query(POST_SELECT_COLUMNS + "WHERE p.id = ?", POST_ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    /** Every post, most recent first - the Blog Feed. */
    public List<Post> findAllPosts() {
        return jdbcTemplate.query(POST_SELECT_COLUMNS + "ORDER BY p.created_at DESC", POST_ROW_MAPPER);
    }

    public Post insertPost(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO posts (author_id, title, content, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, post.getAuthorId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getContent());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        post.setId(generatedId != null ? generatedId.longValue() : null);
        return post;
    }

    public void updatePost(Long id, String title, String content) {
        jdbcTemplate.update(
                "UPDATE posts SET title = ?, content = ?, updated_at = NOW() WHERE id = ?",
                title, content, id
        );
    }

    public void deletePost(Long id) {
        // Comments have a foreign key on posts.id - clear them first so the
        // delete below doesn't fail with a constraint violation.
        jdbcTemplate.update("DELETE FROM comments WHERE post_id = ?", id);
        jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    public void deletePostsAndCommentsForAuthor(Long authorId) {
        jdbcTemplate.update(
                "DELETE FROM comments WHERE post_id IN (SELECT id FROM posts WHERE author_id = ?)", authorId
        );
        jdbcTemplate.update("DELETE FROM comments WHERE author_id = ?", authorId);
        jdbcTemplate.update("DELETE FROM posts WHERE author_id = ?", authorId);
    }

    // ---------------------------------------------------------------------
    // Comments - "Comment System"
    // ---------------------------------------------------------------------

    private static final String COMMENT_SELECT_COLUMNS =
            "SELECT c.id, c.post_id, c.author_id, u.full_name AS author_name, u.role AS author_role, " +
                    "c.content, c.created_at " +
                    "FROM comments c JOIN users u ON u.id = c.author_id ";

    private static final RowMapper<Comment> COMMENT_ROW_MAPPER = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setPostId(rs.getLong("post_id"));
        comment.setAuthorId(rs.getLong("author_id"));
        comment.setAuthorName(rs.getString("author_name"));
        comment.setAuthorRole(rs.getString("author_role"));
        comment.setContent(rs.getString("content"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            comment.setCreatedAt(createdAt.toLocalDateTime());
        }
        return comment;
    };

    public Optional<Comment> findCommentById(Long id) {
        List<Comment> results = jdbcTemplate.query(COMMENT_SELECT_COLUMNS + "WHERE c.id = ?", COMMENT_ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    /** Every comment on one post, oldest first (a conversation reads top-to-bottom). */
    public List<Comment> findCommentsByPost(Long postId) {
        return jdbcTemplate.query(
                COMMENT_SELECT_COLUMNS + "WHERE c.post_id = ? ORDER BY c.created_at ASC",
                COMMENT_ROW_MAPPER, postId
        );
    }

    public Comment insertComment(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO comments (post_id, author_id, content, created_at) VALUES (?, ?, ?, NOW())",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, comment.getPostId());
            ps.setLong(2, comment.getAuthorId());
            ps.setString(3, comment.getContent());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        comment.setId(generatedId != null ? generatedId.longValue() : null);
        return comment;
    }

    public void deleteComment(Long id) {
        jdbcTemplate.update("DELETE FROM comments WHERE id = ?", id);
    }
}
