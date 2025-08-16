package com.consolefire.xpenser.expense.web.error;

import com.consolefire.xpenser.expense.data.exceptions.DuplicateEntryExistsException;
import com.consolefire.xpenser.expense.data.exceptions.EntryDoesNotExistsException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class DefaultHttpExceptionHandler {

    @ExceptionHandler(DuplicateEntryExistsException.class)
    public ResponseEntity<Object> handleDuplicateEntryExistsException(DuplicateEntryExistsException ex) {
        log.error("DuplicateEntryExistsException: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntryDoesNotExistsException.class)
    public ResponseEntity<Object> handleEntryDoesNotExistsException(EntryDoesNotExistsException ex) {
        log.error("EntryDoesNotExistsException: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.error("HttpMediaTypeNotSupportedException: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Unsupported Media Type");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Bad Request");

        // Extract validation errors for a detailed response
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

        body.put("validationErrors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}