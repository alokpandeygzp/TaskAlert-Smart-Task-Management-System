package com.incture.taskmanagement.payloads;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationDto {

    private Long id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private UserDto user;  // The user who will receive the notification
    private TaskDto task;  // The task related to this notification
    // Constructor, getters, and setters omitted for brevity

    public NotificationDto(String message, UserDto user, TaskDto task) {
        this.message = message;
        this.isRead = false;  // default to unread
        this.createdAt = LocalDateTime.now();
        this.user = user;
        this.task = task;
    }
}
