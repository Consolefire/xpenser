package com.consolefire.xpenser.expense.data.exceptions;

public class DuplicateEntryExistsException extends RuntimeException {

    public DuplicateEntryExistsException() {
    }

    public DuplicateEntryExistsException(String message) {
        super(message);
    }

    public DuplicateEntryExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateEntryExistsException(Throwable cause) {
        super(cause);
    }

    public DuplicateEntryExistsException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
