package com.consolefire.xpenser.expenseimport.exception;

public class UnsupportedTemplateFileException extends RuntimeException {

    public UnsupportedTemplateFileException() {
    }

    public UnsupportedTemplateFileException(String message) {
        super(message);
    }

    public UnsupportedTemplateFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedTemplateFileException(Throwable cause) {
        super(cause);
    }

    public UnsupportedTemplateFileException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
