package com.healthcare.platform.blog;

/** Minimal JSON error body for the blog API, e.g. {"message": "..."}. */
public record ErrorResponse(String message) {
}
