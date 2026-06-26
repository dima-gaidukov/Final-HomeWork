package dev.sorokin.eventnotificator.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class NotificationPayloadDto {

    private UUID messageId;

    private String eventType;

    private LocalDateTime occurredAt;

    private Long changedById;

    private Long ownerId;

    private String eventName;

    private List<NotificationChangeDto> changes;

    public NotificationPayloadDto() {
    }

    public NotificationPayloadDto(UUID messageId, String eventType, LocalDateTime occurredAt,
                                  Long changedById, Long ownerId, String eventName,
                                  List<NotificationChangeDto> changes) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.changedById = changedById;
        this.ownerId = ownerId;
        this.eventName = eventName;
        this.changes = changes;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<NotificationChangeDto> getChanges() {
        return changes;
    }

    public void setChanges(List<NotificationChangeDto> changes) {
        this.changes = changes;
    }
}
