package com.healthcare.platform.dto.healthprofile;

/** Minimal JSON error body for the health profile API, e.g. {"message": "..."}. */
public record ErrorResponse(String message) {
}
