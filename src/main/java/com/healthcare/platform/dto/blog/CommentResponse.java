package com.healthcare.platform.dto.blog;
import com.healthcare.platform.model.blog.*;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        Long authorId,
        String authorName,
        String authorRole,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPostId(),
                comment.getAuthorId(),
                comment.getAuthorName(),
                comment.getAuthorRole(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
