package com.consolefire.xpenser.store.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Maps this exception to HTTP 409 Conflict
@Getter
public class ReceiptExistsException extends RuntimeException {

    private final String identifier;
    private final String marketPlaceCode;
    private final String storeCode;

    public ReceiptExistsException(String identifier, String marketPlaceCode, String storeCode) {
        super(String.format("Receipt with identifier '%s', marketplace '%s', and store '%s' already exists.",
            identifier, marketPlaceCode, storeCode));
        this.identifier = identifier;
        this.marketPlaceCode = marketPlaceCode;
        this.storeCode = storeCode;
    }
}