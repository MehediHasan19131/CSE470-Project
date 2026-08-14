package com.healthcare.platform.review;

/** Minimal JSON error body for the review API, e.g. {"message": "..."}. */
public record ErrorResponse(String message) {
}
