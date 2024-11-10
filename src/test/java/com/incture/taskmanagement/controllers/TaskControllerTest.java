package com.incture.taskmanagement.controllers;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.payloads.ApiResponse;
import com.incture.taskmanagement.payloads.TaskDto;
import com.incture.taskmanagement.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        // Initialize mock data
        taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setPriority(Task.Priority.HIGH);
        taskDto.setStatus(Task.Status.PENDING);
    }

    @Test
    void createTask_ShouldReturnTaskDto() {
        Integer userId = 1;

        TaskDto taskDtoToReturn = taskDto;
        when(taskService.createTask(any(Task.class), eq(userId))).thenReturn(taskDtoToReturn);

        TaskDto createdTask = taskController.createTask(taskDto, userId);

        assertNotNull(createdTask);
        // Object expected, Object actual
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals("Test Description", createdTask.getDescription());
        assertEquals(Task.Priority.HIGH, createdTask.getPriority());
    }

    @Test
    void updateTask_ShouldReturnUpdatedTaskDto() {
        Long taskId = 1L;
        Integer userId = 1;

        TaskDto taskDtoToReturn = taskDto;  // Local variable for clarity
        when(taskService.updateTask(eq(taskId), any(Task.class), eq(userId))).thenReturn(taskDtoToReturn);

        TaskDto updatedTask = taskController.updateTask(taskId, taskDto, userId);

        assertNotNull(updatedTask);
        assertEquals("Test Task", updatedTask.getTitle());
    }

    @Test
    void deleteTask_ShouldReturnApiResponse() {
        Long taskId = 1L;

        doNothing().when(taskService).deleteTask(taskId);

        ResponseEntity<ApiResponse> response = taskController.deleteTask(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task is deleted successfully!", response.getBody().getMessage());
    }

    @Test
    void getTaskById_ShouldReturnTaskDto() {
        Long taskId = 1L;

        TaskDto taskDtoToReturn = taskDto;  // Local variable for clarity
        when(taskService.getTaskById(taskId)).thenReturn(taskDtoToReturn);

        ResponseEntity<TaskDto> response = taskController.getTaskById(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Task", response.getBody().getTitle());
    }

    @Test
    void getTaskById_ShouldReturnNotFound() {
        Long taskId = 999L;

        when(taskService.getTaskById(taskId)).thenReturn(null);

        ResponseEntity<TaskDto> response = taskController.getTaskById(taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
