package com.healthcare.platform.blog;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        Long authorId,
        String authorName,
        String authorRole,
        String title,
        String content,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getAuthorId(),
                post.getAuthorName(),
                post.getAuthorRole(),
                post.getTitle(),
                post.getContent(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
