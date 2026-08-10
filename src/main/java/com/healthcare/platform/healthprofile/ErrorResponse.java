package com.healthcare.platform.healthprofile;

/** Minimal JSON error body for the health profile API, e.g. {"message": "..."}. */
public record ErrorResponse(String message) {
}
