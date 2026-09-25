package com.healthcare.platform.model.blog;

import java.time.LocalDateTime;

/**
 * Plain Java object for one row of the `posts` table, used ONLY by
 * {@link BlogJdbcRepository}. No JPA @Entity - same "no ORM" rule the rest
 * of the project follows.
 * <p>
 * {@code authorName}/{@code authorRole} and {@code commentCount} are not
 * columns on `posts` - they're filled in by {@link BlogJdbcRepository}
 * whenever a query joins against `users`/counts `comments`, purely so
 * templates/JSON responses can show a name and a count instead of a raw id
 * and an extra query. Same trick {@code Review.reviewerName} uses.
 */
public class Post {

    private Long id;
    private Long authorId;
    private String authorName;
    private String authorRole;
    private String title;
    private String content;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
