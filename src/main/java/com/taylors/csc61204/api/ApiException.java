package com.taylors.csc61204.api;

/**
 * Thrown when the external trivia API cannot be reached or returns an error.
 * Callers are expected to catch this and present a user-friendly message —
 * never a raw stack trace.
 */
public class ApiException extends Exception {

    private final int statusCode;

    /** Used when the API is unreachable (network failure, timeout, etc). */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    /** Used when the API responds with a non-2xx status code. */
    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isNetworkFailure() {
        return statusCode == -1;
    }
}
