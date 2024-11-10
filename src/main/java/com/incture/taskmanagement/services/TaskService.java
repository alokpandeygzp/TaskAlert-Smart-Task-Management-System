package com.incture.taskmanagement.services;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.Task.Priority;
import com.incture.taskmanagement.entities.Task.Status;
import com.incture.taskmanagement.payloads.TaskDto;

import java.util.List;

public interface TaskService {
    TaskDto createTask(Task task, Integer userId);
    List<TaskDto> getAllTasks();
    TaskDto getTaskById(Long id);
    TaskDto updateTask(Long id, Task taskDetails, Integer userId);
    void deleteTask(Long id);

    // Search and filter tasks
    List<TaskDto> searchTasksByTitle(String title);  // Search by title or description
    List<TaskDto> searchTasksByDesc(String description);
    List<TaskDto> filterTasksByStatus(Status status);  // Filter tasks by status
    List<TaskDto> filterTasksByPriority(Priority priority);  // Filter tasks by priority
}
