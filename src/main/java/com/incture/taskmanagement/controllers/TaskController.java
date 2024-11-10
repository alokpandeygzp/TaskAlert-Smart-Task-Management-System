package com.incture.taskmanagement.controllers;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.Task.Priority;
import com.incture.taskmanagement.entities.Task.Status;

import com.incture.taskmanagement.payloads.ApiResponse;
import com.incture.taskmanagement.payloads.TaskDto;
import com.incture.taskmanagement.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping("/")
    public TaskDto createTask(@RequestBody TaskDto taskDto, @RequestParam Integer userId) {
        // Map TaskDto to Task entity
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setPriority(taskDto.getPriority());
        task.setDeadline(taskDto.getDeadline());
        task.setStatus(taskDto.getStatus());

        return taskService.createTask(task, userId);
    }


    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto, @RequestParam Integer userId) {
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setPriority(taskDto.getPriority());
        task.setDeadline(taskDto.getDeadline());
        task.setStatus(taskDto.getStatus());

        return taskService.updateTask(id, task, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return new ResponseEntity<>(new ApiResponse("Task is deleted successfully!", true), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskDto taskDto = taskService.getTaskById(id);

        // Check if taskDto is null (this case shouldn't happen because of the exception handling in the service)
        if (taskDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(taskDto); // Return TaskDto as response
    }



    @GetMapping("/")
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }



    @GetMapping("/searchByTitle")
    public ResponseEntity<List<TaskDto>> searchTasksByTitle(@RequestParam String title) {
        try {
            List<TaskDto> tasks = taskService.searchTasksByTitle(title);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/searchByDesc")
    public ResponseEntity<List<TaskDto>> searchTasksByDesc(@RequestParam String description) {
        try {
            List<TaskDto> tasks = taskService.searchTasksByDesc(description);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filterByStatus")
    public ResponseEntity<List<TaskDto>> filterTasksByStatus(@RequestParam String status) {
        try {
            Status taskStatus = Status.valueOf(status.toUpperCase());
            List<TaskDto> tasks = taskService.filterTasksByStatus(taskStatus);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filterByPriority")
    public ResponseEntity<List<TaskDto>> filterTasksByPriority(@RequestParam String priority) {
        try {
            Priority taskPriority = Priority.valueOf(priority.toUpperCase());
            List<TaskDto> tasks = taskService.filterTasksByPriority(taskPriority);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
