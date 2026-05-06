package dev.sorokin.eventmanager.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { ResourceNotFoundException.class})
    public ResponseEntity<ErrorMessageResponse> handleException(ResourceNotFoundException ex) {
        ErrorMessageResponse error = new ErrorMessageResponse();
        error.setMessage(ex.getMessage());
        error.setDetailedMessage("Resource not found");
        error.setDateTime(OffsetDateTime.now());

        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
