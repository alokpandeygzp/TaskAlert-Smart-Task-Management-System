package com.incture.taskmanagement.services;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;

@SpringBootTest
class TaskNotificationServiceTest {

    @InjectMocks
    private TaskNotificationService taskNotificationService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    private User assignedUser;
    private Task task;

    @BeforeEach
    void setUp() {

        // Set up a mock user
        assignedUser = new User();
        assignedUser.setId(1);
        assignedUser.setEmail("user@example.com");

        // Set up a mock task with an upcoming deadline
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDeadline(LocalDate.now().plusDays(1));
        task.setAssignedUser(assignedUser);
    }

    @Test
    void testCheckUpcomingDeadlines() {
        // Mock the TaskRepository to return the task with tomorrow's deadline
        when(taskRepository.findTasksByDeadline(LocalDate.now().plusDays(1)))
            .thenReturn(Collections.singletonList(task));

        // Call the scheduled method
        taskNotificationService.checkUpcomingDeadlines();

        // Verify that emailService.sendEmail() was called
        verify(emailService, times(1)).sendEmail(eq("user@example.com"), anyString(), anyString());

        // Verify that notificationService.createTaskDeadlineNotification() was called
        verify(notificationService, times(1))
            .createTaskDeadlineNotification(eq(task), eq(assignedUser.getId()), anyString());

        // Verify that the task was processed and handled correctly
        verify(taskRepository, times(1)).findTasksByDeadline(LocalDate.now().plusDays(1));
    }

    @Test
    void testCheckUpcomingDeadlinesWithNoAssignedUser() {
        // Mock a task without an assigned user
        task.setAssignedUser(null);

        // Mock the TaskRepository to return this task
        when(taskRepository.findTasksByDeadline(LocalDate.now().plusDays(1)))
            .thenReturn(Collections.singletonList(task));

        // Call the scheduled method
        taskNotificationService.checkUpcomingDeadlines();

        // Verify that no email was sent, as there is no assigned user
        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());

        // Verify that no notification was created, as there is no assigned user
        verify(notificationService, times(0))
            .createTaskDeadlineNotification(eq(task), eq(assignedUser.getId()), anyString());
    }

    @Test
    void testCheckUpcomingDeadlinesWithNoEmail() {
        // Mock a task with an assigned user but no email
        assignedUser.setEmail(null);
        task.setAssignedUser(assignedUser);

        // Mock the TaskRepository to return this task
        when(taskRepository.findTasksByDeadline(LocalDate.now().plusDays(1)))
            .thenReturn(Collections.singletonList(task));

        // Call the scheduled method
        taskNotificationService.checkUpcomingDeadlines();

        // Verify that no email was sent, as the assigned user has no email
        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());

        // Verify that no notification was created, as the assigned user has no email
        verify(notificationService, times(0))
            .createTaskDeadlineNotification(eq(task), eq(assignedUser.getId()), anyString());
    }
}
