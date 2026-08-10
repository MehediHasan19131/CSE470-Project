package com.healthcare.platform.service;

/** Thrown when the local Ollama server can't be reached or returns an unexpected response. */
public class AiUnavailableException extends RuntimeException {
    public AiUnavailableException(String message) {
        super(message);
    }

    public AiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
