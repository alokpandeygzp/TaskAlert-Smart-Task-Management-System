package com.incture.taskmanagement.services;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.entities.Task.Priority;
import com.incture.taskmanagement.entities.Task.Status;
import com.incture.taskmanagement.repositories.TaskRepository;
import com.incture.taskmanagement.repositories.UserRepo;
import com.incture.taskmanagement.payloads.TaskDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TaskServiceImplIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepo userRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Create a test user to assign tasks
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setAbout("Test user for task assignment");

        userRepository.save(testUser);

        // Ensure the test user exists
        assertNotNull(userRepository.findById(testUser.getId()).orElse(null));
    }

    @AfterEach
    public void tearDown() {
        taskRepository.deleteAll(); // Clear tasks after each test
        userRepository.deleteAll(); // Delete user after tests
    }

    // Test: Create Task
    @Test
    public void testCreateTask() {
        Task task = new Task();
        task.setTitle("New Task");
        task.setDescription("This is a new task.");
        task.setPriority(Priority.HIGH);
        task.setStatus(Status.PENDING);
        task.setDeadline(LocalDate.of(2024, 12, 31));

        TaskDto createdTaskDto = taskService.createTask(task, testUser.getId());

        assertNotNull(createdTaskDto);
        assertEquals("New Task", createdTaskDto.getTitle());
        assertEquals("This is a new task.", createdTaskDto.getDescription());
        assertEquals(Priority.HIGH, createdTaskDto.getPriority());
        assertEquals(Status.PENDING, createdTaskDto.getStatus());
        assertEquals(LocalDate.of(2024, 12, 31), createdTaskDto.getDeadline());
    }

    // Test: Get Task by ID
    @Test
    public void testGetTaskById() {
        Task task = new Task();
        task.setTitle("Existing Task");
        task.setDescription("This task already exists.");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.COMPLETED);
        task.setDeadline(LocalDate.of(2024, 12, 25));
        task.setAssignedUser(testUser);

        Task savedTask = taskRepository.save(task);
        TaskDto foundTaskDto = taskService.getTaskById(savedTask.getId());

        assertNotNull(foundTaskDto);
        assertEquals("Existing Task", foundTaskDto.getTitle());
        assertEquals("This task already exists.", foundTaskDto.getDescription());
        assertEquals(Priority.MEDIUM, foundTaskDto.getPriority());
        assertEquals(Status.COMPLETED, foundTaskDto.getStatus());
        assertEquals(LocalDate.of(2024, 12, 25), foundTaskDto.getDeadline());
    }

    // Test: Get All Tasks
    @Test
    public void testGetAllTasks() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Task 1 description");
        task1.setPriority(Priority.LOW);
        task1.setStatus(Status.PENDING);
        task1.setDeadline(LocalDate.of(2024, 12, 31));
        task1.setAssignedUser(testUser);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Task 2 description");
        task2.setPriority(Priority.HIGH);
        task2.setStatus(Status.PENDING);
        task2.setDeadline(LocalDate.of(2024, 11, 30));
        task2.setAssignedUser(testUser);

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<TaskDto> taskDtos = taskService.getAllTasks();

        assertNotNull(taskDtos);
        assertEquals(2, taskDtos.size());
    }

    // Test: Update Task
    @Test
    public void testUpdateTask() {
        Task task = new Task();
        task.setTitle("Initial Task");
        task.setDescription("Task to be updated");
        task.setPriority(Priority.MEDIUM);
        task.setStatus(Status.PENDING);
        task.setDeadline(LocalDate.of(2024, 12, 25));
        task.setAssignedUser(testUser);

        Task savedTask = taskRepository.save(task);

        // Update the task
        savedTask.setTitle("Updated Task");
        savedTask.setDescription("Updated task description");
        savedTask.setPriority(Priority.HIGH);

        TaskDto updatedTaskDto = taskService.updateTask(savedTask.getId(), savedTask, testUser.getId());

        assertNotNull(updatedTaskDto);
        assertEquals("Updated Task", updatedTaskDto.getTitle());
        assertEquals("Updated task description", updatedTaskDto.getDescription());
        assertEquals(Priority.HIGH, updatedTaskDto.getPriority());
    }

    // Test: Delete Task
    @Test
    public void testDeleteTask() {
        Task task = new Task();
        task.setTitle("Task to be deleted");
        task.setDescription("This task will be deleted.");
        task.setPriority(Priority.LOW);
        task.setStatus(Status.PENDING);
        task.setDeadline(LocalDate.of(2024, 12, 31));
        task.setAssignedUser(testUser);

        Task savedTask = taskRepository.save(task);
        taskService.deleteTask(savedTask.getId());

        Optional<Task> deletedTask = taskRepository.findById(savedTask.getId());
        assertFalse(deletedTask.isPresent());
    }

    // Test: Search Tasks by Title
    @Test
    public void testSearchTasksByTitle() {
        Task task1 = new Task();
        task1.setTitle("Find me");
        task1.setDescription("Task with title 'Find me'");
        task1.setPriority(Priority.HIGH);
        task1.setStatus(Status.PENDING);
        task1.setDeadline(LocalDate.of(2024, 12, 31));
        task1.setAssignedUser(testUser);

        Task task2 = new Task();
        task2.setTitle("Search me");
        task2.setDescription("Task with title 'Search me'");
        task2.setPriority(Priority.LOW);
        task2.setStatus(Status.COMPLETED);
        task2.setDeadline(LocalDate.of(2024, 11, 30));
        task2.setAssignedUser(testUser);

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<TaskDto> foundTasks = taskService.searchTasksByTitle("find");

        assertNotNull(foundTasks);
        assertEquals(1, foundTasks.size());
        assertEquals("Find me", foundTasks.get(0).getTitle());
    }

    // Test: Filter Tasks by Status
    @Test
    public void testFilterTasksByStatus() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Pending task");
        task1.setPriority(Priority.MEDIUM);
        task1.setStatus(Status.PENDING);
        task1.setDeadline(LocalDate.of(2024, 12, 31));
        task1.setAssignedUser(testUser);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Completed task");
        task2.setPriority(Priority.HIGH);
        task2.setStatus(Status.COMPLETED);
        task2.setDeadline(LocalDate.of(2024, 11, 30));
        task2.setAssignedUser(testUser);

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<TaskDto> filteredTasks = taskService.filterTasksByStatus(Status.PENDING);

        assertNotNull(filteredTasks);
        assertEquals(1, filteredTasks.size());
        assertEquals("Task 1", filteredTasks.get(0).getTitle());
    }
}
