package com.incture.taskmanagement.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private boolean isRead;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // The user who will receive the notification

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;  // The task related to this notification

    // Constructor, getters, and setters omitted for brevity

    public Notification(String message, User user, Task task) {
        this.message = message;
        this.isRead = false;  // default to unread
        this.createdAt = LocalDateTime.now();
        this.user = user;
        this.task = task;
    }
}
