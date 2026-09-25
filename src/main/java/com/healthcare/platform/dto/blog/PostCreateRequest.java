package com.healthcare.platform.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** What we receive from {@code POST /api/blog/posts} - "Create Post". */
public record PostCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 8000) String content
) {
}
