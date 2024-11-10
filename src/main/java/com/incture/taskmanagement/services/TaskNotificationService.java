package com.incture.taskmanagement.services;

import com.incture.taskmanagement.entities.Task;
import com.incture.taskmanagement.repositories.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskNotificationService {

    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public TaskNotificationService(TaskRepository taskRepository, EmailService emailService, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    // This method will be called every day at 00:00 (according to your cron expression)
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkUpcomingDeadlines() {
        // Log that the scheduled task has started
        System.out.println("Scheduled task started at: " + LocalDateTime.now());

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // Query the tasks with tomorrow's deadline
        List<Task> tasks = taskRepository.findTasksByDeadline(tomorrow);
        System.out.println("Tasks for tomorrow's deadline: " + tasks.size());  // Log the number of tasks found

        // Loop through each task and send email notifications if necessary
        for (Task task : tasks) {
            String emailSubject = "Upcoming Task Deadline: " + task.getTitle();
            String emailBody = "Dear User,\n\nThe task '" + task.getTitle() + "' has an upcoming deadline on "
                    + task.getDeadline() + ". Please ensure it is completed on time.\n\nBest regards,\nTask Management System";

            // Ensure the task has an assigned user and that the user has a valid email
            if (task.getAssignedUser() != null && task.getAssignedUser().getEmail() != null) {

                //Send Email
                emailService.sendEmail(task.getAssignedUser().getEmail(), emailSubject, emailBody);
                System.out.println("Email sent to: " + task.getAssignedUser().getEmail());

                // Create a notification for the user to show in the app
                String message = "The task '" + task.getTitle() + "' has an upcoming deadline on " + task.getDeadline();
                notificationService.createTaskDeadlineNotification(task, task.getAssignedUser().getId(), message);
                System.out.println("Notification created for: " + task.getAssignedUser().getEmail());

            } else {
                // Log if there is no assigned user or email
                System.err.println("No assigned user or email found for task: " + task.getTitle());
            }
        }
    }
}
