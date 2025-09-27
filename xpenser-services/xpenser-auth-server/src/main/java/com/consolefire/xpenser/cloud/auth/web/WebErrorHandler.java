package com.consolefire.xpenser.cloud.auth.web;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebErrorHandler {

    /**
     * Handles IllegalArgumentException, typically thrown by the service layer
     * when mandatory fields are missing or constraints are violated (e.g., Assert checks).
     * Maps these to HTTP 400 Bad Request.
     *
     * @param ex the exception thrown
     * @return a ResponseEntity containing the error details and 400 status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Caught IllegalArgumentException: {}", ex.getMessage());

        // Construct a simple error response body
        Map<String, String> errorResponse = Map.of(
            "error", "invalid_request",
            "error_description", ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
