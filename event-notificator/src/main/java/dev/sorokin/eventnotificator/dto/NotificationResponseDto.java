package dev.sorokin.eventnotificator.dto;

import java.time.LocalDateTime;

public class NotificationResponseDto {

    private Long notificationId;

    private String type;

    private Long eventId;

    private LocalDateTime createdAt;

    private boolean isRead;

    private String message;

    private NotificationPayloadDto payload;

    public NotificationResponseDto() {
    }

    public NotificationResponseDto(Long notificationId, String type, Long eventId,
                                   LocalDateTime createdAt, boolean isRead,
                                   String message, NotificationPayloadDto payload) {
        this.notificationId = notificationId;
        this.type = type;
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.message = message;
        this.payload = payload;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationPayloadDto getPayload() {
        return payload;
    }

    public void setPayload(NotificationPayloadDto payload) {
        this.payload = payload;
    }
}
