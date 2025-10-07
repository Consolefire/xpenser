package com.consolefire.xpenser.expenseimport.api;

import com.consolefire.xpenser.expenseimport.data.exception.DuplicateRecordFoundException;
import com.consolefire.xpenser.expenseimport.data.exception.RecordNotFoundException;
import com.consolefire.xpenser.expenseimport.exception.UnsupportedTemplateFileException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = {RestController.class})
public class DefaultRestErrorHandler extends ResponseEntityExceptionHandler {

    // Handler for 404 Not Found exceptions
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    // Handler for 409 Conflict exceptions
    @ExceptionHandler(DuplicateRecordFoundException.class)
    public ResponseEntity<Object> handleDuplicateRecordFoundException(DuplicateRecordFoundException ex,
        WebRequest request) {
        String errorMessage = ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT); // HTTP 409
    }

    // Handler for 400 Bad Request/Internal Server Error exceptions due to file issues
    @ExceptionHandler(UnsupportedTemplateFileException.class)
    public ResponseEntity<Object> handleUnsupportedTemplateFileException(UnsupportedTemplateFileException ex,
        WebRequest request) {
        String errorMessage = ex.getMessage();

        // Determine status: 400 for client-side issues (empty file), 500 for server-side read issues (IOException)
        HttpStatus status =
            ex.getCause() instanceof IOException ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(errorMessage, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // The default handler uses 400 Bad Request, but we explicitly use 406 Not Acceptable as requested
        return new ResponseEntity<>(errors, HttpStatus.NOT_ACCEPTABLE);
    }
}