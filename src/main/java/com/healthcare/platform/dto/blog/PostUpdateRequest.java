package com.healthcare.platform.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * What we receive from {@code PUT /api/blog/posts/{id}} - editing your own
 * post. Not one of the two assigned backend items ("Create Post", "Comment
 * System") - included as a small extra the same way Sprint 3 added delete
 * endpoints beyond "Add History"/"Update History". See README.
 */
public record PostUpdateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 8000) String content
) {
}
