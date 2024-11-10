package com.incture.taskmanagement.services.impl;

import com.incture.taskmanagement.entities.Notification;
import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.Task.Priority;
import com.incture.taskmanagement.entities.Task.Status;
import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.exceptions.ApiException;
import com.incture.taskmanagement.exceptions.ResourceNotFoundException;
import com.incture.taskmanagement.payloads.RoleDto;
import com.incture.taskmanagement.payloads.TaskDto;
import com.incture.taskmanagement.payloads.UserDto;
import com.incture.taskmanagement.repositories.NotificationRepository;
import com.incture.taskmanagement.repositories.TaskRepository;
import com.incture.taskmanagement.repositories.UserRepo;
import com.incture.taskmanagement.services.NotificationService;
import com.incture.taskmanagement.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;
    private final UserRepo userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepo userRepository, NotificationService notificationService, NotificationRepository notificationRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public TaskDto createTask(Task task, Integer userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                logger.error("User with ID {} not found for task creation", userId);
                throw new ResourceNotFoundException("User", "Id", userId);
            }
            task.setAssignedUser(userOpt.get());
            Task savedTask = taskRepository.save(task);

            // After saving the task, create a notification for the user
            notificationService.createTaskAssignmentNotification(savedTask, userId);
            logger.info("Task with ID {} created and assigned to user {}", savedTask.getId(), userId);

            return toTaskDto(savedTask);  // Return TaskDto instead of Task entity
        }
        catch (Exception e) {
            logger.error("Error occurred while creating task: {}", e.getMessage(), e);
            throw new ApiException("Error creating task");
        }
    }


    @Override
    public List<TaskDto> getAllTasks() {
        try {
            List<Task> tasks = taskRepository.findAll();

            // Convert all tasks to TaskDto
            return tasks.stream()
                    .map(this::toTaskDto)
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            logger.error("Error occurred while retrieving all tasks: {}", e.getMessage(), e);
            throw new ApiException("Error fetching tasks");
        }
    }

    @Override
    public TaskDto getTaskById(Long id) {
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ApiException("Task not found"));
            return toTaskDto(task);
        }
        catch (Exception e) {
            logger.error("Error occurred while fetching task with ID {}: {}", id, e.getMessage(), e);
            throw new ApiException("Error fetching task");
        }
    }


    public TaskDto updateTask(Long id, Task taskDetails, Integer userId) {
        try {
            if (!taskRepository.existsById(id)) {
                logger.error("Task with ID {} not found for update", id);
                throw new ApiException("Task not found");
            }
            // Fetch the assigned user from the database using userId
            User assignedUser = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("User with ID {} not found for task update", userId);
                        return new ApiException("User not found");
                    });

            // Set the assigned user to the task
            taskDetails.setAssignedUser(assignedUser);

            // Ensure the task ID is not overwritten
            taskDetails.setId(id);
            Task updatedTask = taskRepository.save(taskDetails);

            // After updating the task, create notifications for the user and admins
            notificationService.createTaskUpdateNotification(updatedTask);

            logger.info("Task with ID {} updated and assigned to user {}", updatedTask.getId(), userId);

            // Return updated task as TaskDto
            return toTaskDto(updatedTask);
        }
        catch (Exception e) {
            logger.error("Error occurred while updating task with ID {}: {}", id, e.getMessage(), e);
            throw new ApiException("Error updating task");
        }
    }

    @Override
    public void deleteTask(Long id) {
        try {
            if (!taskRepository.existsById(id)) {
                logger.error("Task with ID {} not found for deletion", id);
                throw new ApiException("Task not found");
            }
            // Retrieve task and associated notifications
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new ApiException("Task not found"));

            // Clear or delete related notifications
            List<Notification> notifications = notificationRepository.findByTask(task);
            if (!notifications.isEmpty()) {
                // Option 1: Clear the task reference in notifications
                //notifications.forEach(notification -> notification.setTask(null));
                //notificationRepository.saveAll(notifications);  // Save changes to the notifications
                //logger.info("Cleared task reference in related notifications for task ID {}", id);

                // Option 2: Alternatively, delete related notifications if they should be removed
                 notificationRepository.deleteAll(notifications);
                 logger.info("Deleted related notifications for task ID {}", id);
            }

            taskRepository.deleteById(id);
            logger.info("Task with ID {} deleted", id);
        }
        catch (Exception e) {
            logger.error("Error occurred while deleting task with ID {}: {}", id, e.getMessage(), e);
            throw new ApiException("Error deleting task");
        }
    }

    @Override
    public List<TaskDto> searchTasksByTitle(String title) {
        try {
            List<Task> tasks = taskRepository.findByTitleContainingIgnoreCase(title);
            return tasks.stream()
                    .map(this::toTaskDto)
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            logger.error("Error occurred while searching tasks by title: {}", title, e);
            throw new ApiException("Error searching tasks by title");
        }
    }

    @Override
    public List<TaskDto> searchTasksByDesc(String description) {
        try {
            List<Task> tasks = taskRepository.findByDescriptionContainingIgnoreCase(description);
            return tasks.stream()
                    .map(this::toTaskDto)
                    .collect(Collectors.toList());
        }  catch (Exception e) {
            logger.error("Error occurred while searching tasks by description: {}", description, e);
            throw new ApiException("Error searching tasks by description");
        }
    }

    @Override
    public List<TaskDto> filterTasksByStatus(Status status) {
        try {
            List<Task> tasks = taskRepository.findByStatus(status);
            return tasks.stream()
                    .map(this::toTaskDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while filtering tasks by status: {}", status, e);
            throw new ApiException("Error filtering tasks by status");
        }
    }

    @Override
    public List<TaskDto> filterTasksByPriority(Priority priority) {
        try {
            List<Task> tasks = taskRepository.findByPriority(priority);
            return tasks.stream()
                    .map(this::toTaskDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error occurred while filtering tasks by priority: {}", priority, e);
            throw new ApiException("Error filtering tasks by priority");
        }
    }





    // Utility method to map User to UserDto
    private UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setAbout(user.getAbout());

        // Populate roles if the user has roles
        if (user.getRoles() != null) {
            Set<RoleDto> roleDtos = user.getRoles().stream()
                    .map(role -> {
                        RoleDto roleDto = new RoleDto();
                        roleDto.setId(role.getId());
                        roleDto.setName(role.getName());
                        return roleDto;
                    })
                    .collect(Collectors.toSet());
            userDto.setRoles(roleDtos);
        }

        return userDto;
    }
    // Utility method to map Task to TaskDto
    private TaskDto toTaskDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setPriority(task.getPriority());
        taskDto.setDeadline(task.getDeadline());
        taskDto.setStatus(task.getStatus());
        taskDto.setAssignedUser(toUserDto(task.getAssignedUser()));
        return taskDto;
    }

}
