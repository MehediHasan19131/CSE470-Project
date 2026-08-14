package com.healthcare.platform.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** What we receive from {@code POST /api/blog/posts/{postId}/comments} - "Comment System". */
public record CommentCreateRequest(
        @NotBlank @Size(max = 1000) String content
) {
}
