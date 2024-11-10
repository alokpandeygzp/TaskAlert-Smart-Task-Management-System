package com.incture.taskmanagement.repositories;

import com.incture.taskmanagement.entities.Notification;
import com.incture.taskmanagement.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Find all notifications for a user and order them by createdAt (latest first)
    List<Notification> findByUserIdAndIsRead(Integer userId, boolean isRead, Sort sort);

    // Find all unread notifications for a user and order them by createdAt (latest first)
    List<Notification> findByUserIdAndIsReadFalse(Integer userId, Sort sort);

    List<Notification> findByTask(Task task);

    // Fetch notifications by userId, regardless of the is_read status
    // List<Notification> findByUserId(Integer userId);
}
