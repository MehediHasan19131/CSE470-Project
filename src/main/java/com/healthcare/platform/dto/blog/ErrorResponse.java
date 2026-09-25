package com.healthcare.platform.dto.blog;

/** Minimal JSON error body for the blog API, e.g. {"message": "..."}. */
public record ErrorResponse(String message) {
}
