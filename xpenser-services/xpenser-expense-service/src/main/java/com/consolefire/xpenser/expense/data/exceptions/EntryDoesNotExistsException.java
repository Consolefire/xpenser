package com.consolefire.xpenser.expense.data.exceptions;

public class EntryDoesNotExistsException extends RuntimeException {

    public EntryDoesNotExistsException() {
    }

    public EntryDoesNotExistsException(String message) {
        super(message);
    }

    public EntryDoesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntryDoesNotExistsException(Throwable cause) {
        super(cause);
    }

    public EntryDoesNotExistsException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
