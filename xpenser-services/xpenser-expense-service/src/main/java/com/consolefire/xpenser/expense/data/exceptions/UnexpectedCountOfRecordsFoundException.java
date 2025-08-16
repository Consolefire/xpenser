package com.consolefire.xpenser.expense.data.exceptions;

import static java.lang.String.format;

public class UnexpectedCountOfRecordsFoundException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "Expected %d records but found %d instead!";

    public UnexpectedCountOfRecordsFoundException(int expectedSize, int size) {
        super(format(MESSAGE_FORMAT, expectedSize, size));
    }
}
