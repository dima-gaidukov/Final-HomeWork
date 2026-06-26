package dev.sorokin.eventnotificator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { AccessDeniedException.class})
    public ResponseEntity<ErrorMessageResponse> handleException(AccessDeniedException ex) {

        ErrorMessageResponse error = new ErrorMessageResponse();
        error.setMessage(ex.getMessage());
        error.setDetailedMessage("Access Denied");
        error.setDateTime(OffsetDateTime.now());
        return  ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class})
    public ResponseEntity<ErrorMessageResponse> handleBadRequest(IllegalArgumentException ex) {

        ErrorMessageResponse error = new ErrorMessageResponse();
        error.setMessage(ex.getMessage());
        error.setDetailedMessage("Bad request");
        error.setDateTime(OffsetDateTime.now());
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorMessageResponse> handleBadRequest(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(it -> it.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining("; "));

        ErrorMessageResponse error = new ErrorMessageResponse();
        error.setMessage(errors);
        error.setDetailedMessage("Bad request");
        error.setDateTime(OffsetDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
