package com.incture.taskmanagement.services;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.Task.Status;
import com.incture.taskmanagement.entities.Task.Priority;
import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.exceptions.ApiException;
import com.incture.taskmanagement.exceptions.ResourceNotFoundException;
import com.incture.taskmanagement.payloads.TaskDto;
import com.incture.taskmanagement.repositories.TaskRepository;
import com.incture.taskmanagement.repositories.UserRepo;
import com.incture.taskmanagement.services.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepo userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        // Mock user setup
        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        // Mock task setup
        task = new Task();
        task.setId(1L);
        task.setTitle("Task 1");
        task.setDescription("Description of Task 1");
        task.setPriority(Task.Priority.HIGH);
        task.setDeadline(java.time.LocalDate.now().plusDays(5));
        task.setStatus(Task.Status.PENDING);
        task.setAssignedUser(user);
    }

    @Test
    void createTask_shouldCreateTask_whenValidData() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDto result = taskService.createTask(task, 1);

        // Assert
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription());
        assertEquals(user.getId(), result.getAssignedUser().getId());
        verify(notificationService).createTaskAssignmentNotification(any(Task.class), eq(1));
    }

    @Test
    void createTask_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(task, 1));
        assertEquals("User", exception.getResourceName());
        assertEquals("Id", exception.getFieldName());
        assertEquals(1, exception.getFieldValue());
    }

    @Test
    void getTaskById_shouldReturnTask_whenTaskExists() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act
        TaskDto result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getTitle(), result.getTitle());
    }

    @Test
    void getTaskById_shouldThrowException_whenTaskNotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> taskService.getTaskById(1L));
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void updateTask_shouldUpdateTask_whenValidData() {
        // Arrange
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setPriority(Task.Priority.MEDIUM);
        updatedTask.setStatus(Task.Status.COMPLETED);
        updatedTask.setAssignedUser(user);

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        TaskDto result = taskService.updateTask(1L, updatedTask, 1);

        // Assert
        assertNotNull(result);
        assertEquals(updatedTask.getId(), result.getId());
        assertEquals(updatedTask.getTitle(), result.getTitle());
        assertEquals(updatedTask.getStatus(), result.getStatus());
        verify(notificationService).createTaskUpdateNotification(any(Task.class));
    }

    @Test
    void updateTask_shouldThrowException_whenTaskNotFound() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> taskService.updateTask(1L, task, 1));
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void deleteTask_shouldDeleteTask_whenTaskExists() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_shouldThrowException_whenTaskNotFound() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ApiException exception = assertThrows(ApiException.class, () -> taskService.deleteTask(1L));
        assertEquals("Task not found", exception.getMessage());
    }

    // Additional tests for other methods like search, filter, etc.

    // --- Search by Title ---
    @Test
    void searchTasksByTitle_shouldReturnTasks_whenTitleMatches() {
        // Arrange
        String title = "Task 1";
        when(taskRepository.findByTitleContainingIgnoreCase(title)).thenReturn(List.of(task));

        // Act
        List<TaskDto> result = taskService.searchTasksByTitle(title);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getTitle(), result.get(0).getTitle());
    }

    @Test
    void searchTasksByTitle_shouldReturnEmptyList_whenTitleDoesNotMatch() {
        // Arrange
        String title = "Non-existing Task";
        when(taskRepository.findByTitleContainingIgnoreCase(title)).thenReturn(List.of());

        // Act
        List<TaskDto> result = taskService.searchTasksByTitle(title);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Search by Description ---
    @Test
    void searchTasksByDesc_shouldReturnTasks_whenDescriptionMatches() {
        // Arrange
        String description = "Description of Task 1";
        when(taskRepository.findByDescriptionContainingIgnoreCase(description)).thenReturn(List.of(task));

        // Act
        List<TaskDto> result = taskService.searchTasksByDesc(description);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getDescription(), result.get(0).getDescription());
    }

    @Test
    void searchTasksByDesc_shouldReturnEmptyList_whenDescriptionDoesNotMatch() {
        // Arrange
        String description = "Non-existing Description";
        when(taskRepository.findByDescriptionContainingIgnoreCase(description)).thenReturn(List.of());

        // Act
        List<TaskDto> result = taskService.searchTasksByDesc(description);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Filter Tasks by Status ---
    @Test
    void filterTasksByStatus_shouldReturnTasks_whenStatusMatches() {
        // Arrange
        Status status = Status.PENDING;
        when(taskRepository.findByStatus(status)).thenReturn(List.of(task));

        // Act
        List<TaskDto> result = taskService.filterTasksByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getStatus(), result.get(0).getStatus());
    }

    @Test
    void filterTasksByStatus_shouldReturnEmptyList_whenNoTasksMatchStatus() {
        // Arrange
        Status status = Status.COMPLETED;
        when(taskRepository.findByStatus(status)).thenReturn(List.of());

        // Act
        List<TaskDto> result = taskService.filterTasksByStatus(status);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Filter Tasks by Priority ---
    @Test
    void filterTasksByPriority_shouldReturnTasks_whenPriorityMatches() {
        // Arrange
        Priority priority = Priority.HIGH;
        when(taskRepository.findByPriority(priority)).thenReturn(List.of(task));

        // Act
        List<TaskDto> result = taskService.filterTasksByPriority(priority);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(task.getPriority(), result.get(0).getPriority());
    }

    @Test
    void filterTasksByPriority_shouldReturnEmptyList_whenNoTasksMatchPriority() {
        // Arrange
        Priority priority = Priority.LOW;
        when(taskRepository.findByPriority(priority)).thenReturn(List.of());

        // Act
        List<TaskDto> result = taskService.filterTasksByPriority(priority);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
