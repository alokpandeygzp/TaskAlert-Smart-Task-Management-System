package com.incture.taskmanagement.services.impl;

import com.incture.taskmanagement.entities.Notification;
import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.exceptions.ResourceNotFoundException;
import com.incture.taskmanagement.payloads.NotificationDto;
import com.incture.taskmanagement.repositories.NotificationRepository;
import com.incture.taskmanagement.repositories.UserRepo;
import com.incture.taskmanagement.services.NotificationService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository notificationRepository;
    private final UserRepo userRepository;
    private final ModelMapper modelMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepo userRepository, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    // Method to create a notification when a task is assigned to a user
    public void createTaskAssignmentNotification(Task task, Integer userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));

            String message = "You have been assigned a new task: " + task.getTitle();
            Notification notification = new Notification(message, user, task);

            notificationRepository.save(notification);
            logger.info("Notification created for user ID {} about task assignment: {}", userId, task.getTitle());
        }
        catch (Exception e) {
            logger.error("Error occurred while creating task assignment notification: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating task assignment notification", e);
        }
    }

    // Method to create a notification for task deadline
    public void createTaskDeadlineNotification(Task task, Integer userId, String message) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Notification notification = new Notification(message, user, task);
            notificationRepository.save(notification);
            logger.info("Notification created for user ID {} about task deadline: {}", userId, task.getTitle());
        }
        catch (Exception e) {
            logger.error("Error occurred while creating task deadline notification: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating task deadline notification", e);
        }
    }


    // Method to create a notification for task updates and notify admins
    public void createTaskUpdateNotification(Task task) {
        try {
            // Notify the admin users
            List<User> adminUsers = userRepository.findByRoles_Name("ADMIN_USER"); // Find all admin users
            for (User admin : adminUsers) {
                Notification adminNotification = new Notification(
                        "The task " + task.getTitle() + " has been completed.",
                        admin, task
                );
                notificationRepository.save(adminNotification);
                logger.info("Admin notification created for task update: {}", task.getTitle());
            }
        }
        catch (Exception e) {
            logger.error("Error occurred while creating task update notifications: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating task update notifications", e);
        }
    }


    // Method to mark a notification as read
    public void markNotificationAsRead(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found"));
            notification.setRead(true);
            notificationRepository.save(notification);
            logger.info("Notification with ID {} marked as read", notificationId);
        }
        catch (Exception e) {
            logger.error("Error occurred while marking notification as read: {}", e.getMessage(), e);
            throw new RuntimeException("Error marking notification as read", e);
        }
    }

    // Fetch unread notifications for a user
    public List<NotificationDto> getUnreadNotifications(Integer userId) {
        try {
            Sort sortByCreatedAtDesc = Sort.by(Sort.Order.desc("createdAt"));
            List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId, sortByCreatedAtDesc);
            logger.info("Fetched {} unread notifications for user ID {}", notifications.size(), userId);

            return notifications.stream()
                    .map(notification -> this.modelMapper.map(notification, NotificationDto.class))
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            logger.error("Error occurred while fetching unread notifications for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error fetching unread notifications", e);
        }
    }

    // Fetch all notifications for a user
    public List<NotificationDto> getAllNotifications(Integer userId) {
        try {
            Sort sortByCreatedAtDesc = Sort.by(Sort.Order.desc("createdAt"));
            // Fetch all notifications, checking that is_read is boolean
            List<Notification> notifications = notificationRepository.findByUserIdAndIsRead(userId, true, sortByCreatedAtDesc);

            logger.info("Fetched {} read notifications for user ID {}", notifications.size(), userId);
            return notifications.stream()
                    .map(notification -> this.modelMapper.map(notification, NotificationDto.class))
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            logger.error("Error occurred while fetching all notifications for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error fetching all notifications", e);
        }
    }
}
