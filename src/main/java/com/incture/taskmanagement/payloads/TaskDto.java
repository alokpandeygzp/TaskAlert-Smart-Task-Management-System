package com.incture.taskmanagement.payloads;

import com.incture.taskmanagement.entities.Task.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate deadline;
    private UserDto assignedUser;
    private Status status;

    // Add getters and setters here
}

