package dev.sorokin.eventnotificator.dto;

import java.util.List;

public class MarkNotificationsAsReadRequest {

    private List<Long> notificationIds;

    public MarkNotificationsAsReadRequest() {
    }

    public MarkNotificationsAsReadRequest(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }

    public List<Long> getNotificationIds() {
        return notificationIds;
    }

    public void setNotificationIds(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
    }
}
