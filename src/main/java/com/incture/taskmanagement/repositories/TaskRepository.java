package com.incture.taskmanagement.repositories;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.entities.Task.Priority;
import com.incture.taskmanagement.entities.Task.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(Status status);
    List<Task> findByPriority(Priority priority);
    List<Task> findByTitleContainingIgnoreCase(String title);
    List<Task> findByDescriptionContainingIgnoreCase(String description);
    List<Task> findTasksByDeadline(LocalDate deadline);
    void deleteAllByAssignedUserId(int userId);
}
