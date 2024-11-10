package com.incture.taskmanagement.services;

import com.incture.taskmanagement.entities.Notification;
import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.payloads.NotificationDto;
import com.incture.taskmanagement.repositories.NotificationRepository;
import com.incture.taskmanagement.repositories.UserRepo;
import com.incture.taskmanagement.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepo userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Task task;
    private Notification notification;
    private NotificationDto notificationDto;

    @BeforeEach
    void setUp() {
        // Setting up test data
        user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setName("Test User");


        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        notification = new Notification("Test message", user, task);
        notification.setId(1L);
        notification.setCreatedAt(LocalDateTime.now());

        notificationDto = new NotificationDto("Test message", null, null);
    }

    @Test
    void createTaskAssignmentNotification_shouldSaveNotification() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(user));

        // Act
        notificationService.createTaskAssignmentNotification(task, 1);

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createTaskDeadlineNotification_shouldSaveNotification() {
        String message = "The task deadline is approaching!";
        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(user));

        notificationService.createTaskDeadlineNotification(task, 1, message);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createTaskUpdateNotification_shouldNotifyAllAdmins() {
        User adminUser = new User();
        adminUser.setId(2);
        adminUser.setName("Admin User");

        when(userRepository.findByRoles_Name("ADMIN_USER")).thenReturn(List.of(adminUser));

        notificationService.createTaskUpdateNotification(task);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void markNotificationAsRead_shouldUpdateNotificationStatus() {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setRead(false);

        when(notificationRepository.findById(1L)).thenReturn(java.util.Optional.of(notification));

        notificationService.markNotificationAsRead(1L);

        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }


    @Test
    void testGetUnreadNotifications() {
        // Prepare test data
        when(notificationRepository.findByUserIdAndIsReadFalse(anyInt(), any())).thenReturn(Arrays.asList(notification));
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(notificationDto);

        // Call the service method
        List<NotificationDto> result = notificationService.getUnreadNotifications(1);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test message", result.get(0).getMessage());
        verify(notificationRepository, times(1)).findByUserIdAndIsReadFalse(anyInt(), any());
    }


    @Test
    void testGetAllNotifications() {
        // Prepare test data
        when(notificationRepository.findByUserIdAndIsRead(anyInt(), eq(true), any())).thenReturn(Arrays.asList(notification));
        when(modelMapper.map(notification, NotificationDto.class)).thenReturn(notificationDto);

        // Call the service method
        List<NotificationDto> result = notificationService.getAllNotifications(1);

        // Verify the result
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test message", result.get(0).getMessage());
        verify(notificationRepository, times(1)).findByUserIdAndIsRead(anyInt(), eq(true), any());
    }
}
