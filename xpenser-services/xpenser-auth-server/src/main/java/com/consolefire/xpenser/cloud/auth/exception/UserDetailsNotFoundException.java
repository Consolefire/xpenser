package com.consolefire.xpenser.cloud.auth.exception;

public class UserDetailsNotFoundException extends RuntimeException {

    public UserDetailsNotFoundException() {
    }

    public UserDetailsNotFoundException(String message) {
        super(message);
    }

    public UserDetailsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDetailsNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserDetailsNotFoundException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
