package dev.sorokin.eventnotificator.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @JoinColumn(name = "payload_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private NotificationEventPayloadEntity payload;

    public NotificationEntity() {
    }

    public NotificationEntity(Long id, Long userId, Boolean isRead, LocalDateTime createdAt,
                              LocalDateTime readAt, NotificationEventPayloadEntity payload) {
        this.id = id;
        this.userId = userId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.payload = payload;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public NotificationEventPayloadEntity getPayload() {
        return payload;
    }

    public void setPayload(NotificationEventPayloadEntity payload) {
        this.payload = payload;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}
