package com.consolefire.xpenser.expenseimport.data.exception;

public class DuplicateRecordFoundException extends RuntimeException {

    public DuplicateRecordFoundException() {
    }

    public DuplicateRecordFoundException(String message) {
        super(message);
    }

    public DuplicateRecordFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateRecordFoundException(Throwable cause) {
        super(cause);
    }

    public DuplicateRecordFoundException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
