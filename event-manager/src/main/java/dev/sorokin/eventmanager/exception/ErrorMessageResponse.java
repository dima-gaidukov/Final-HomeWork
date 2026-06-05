package dev.sorokin.eventmanager.exception;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ErrorMessageResponse {
    private String message;
    private String detailedMessage;
    private OffsetDateTime dateTime;

}
