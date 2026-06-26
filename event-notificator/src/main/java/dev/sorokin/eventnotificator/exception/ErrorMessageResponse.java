package dev.sorokin.eventnotificator.exception;



import java.time.OffsetDateTime;


public class ErrorMessageResponse {
    private String message;
    private String detailedMessage;
    private OffsetDateTime dateTime;

    public ErrorMessageResponse() {
    }

    public ErrorMessageResponse(String message, String detailedMessage, OffsetDateTime dateTime) {
        this.message = message;
        this.detailedMessage = detailedMessage;
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
