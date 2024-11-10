package com.incture.taskmanagement.services;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.payloads.NotificationDto;

import java.util.List;

public interface NotificationService {

    // Method to create a notification when a task is assigned to a user
    void createTaskAssignmentNotification(Task task, Integer userId);

    // Method to create a notification for task deadline
    void createTaskDeadlineNotification(Task task, Integer userId, String message);

    // Method to create a notification for task updates and notify admins
    void createTaskUpdateNotification(Task task);

    // Method to mark a notification as read
    void markNotificationAsRead(Long notificationId);

    // Fetch unread notifications for a user
    List<NotificationDto> getUnreadNotifications(Integer userId);

    // Fetch all notifications for a user
    List<NotificationDto> getAllNotifications(Integer userId);
}
