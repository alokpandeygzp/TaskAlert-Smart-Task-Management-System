package com.incture.taskmanagement.controllers;

import com.incture.taskmanagement.exceptions.ResourceNotFoundException;
import com.incture.taskmanagement.payloads.NotificationDto;
import com.incture.taskmanagement.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void getUnreadNotifications_ShouldReturnListOfNotifications() throws Exception {
        // Given
        NotificationDto notification1 = new NotificationDto("Test message 1", null, null);
        NotificationDto notification2 = new NotificationDto("Test message 2", null, null);
        List<NotificationDto> notifications = Arrays.asList(notification1, notification2);

        when(notificationService.getUnreadNotifications(1)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/notifications/unread/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Test message 1"))
                .andExpect(jsonPath("$[1].message").value("Test message 2"));

        verify(notificationService, times(1)).getUnreadNotifications(1);
    }

    @Test
    void markNotificationAsRead_ShouldReturnNoContent() throws Exception {
        // Given
        Long notificationId = 1L;

        doNothing().when(notificationService).markNotificationAsRead(notificationId);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/notifications/read/{notificationId}", notificationId))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).markNotificationAsRead(notificationId);
    }

    @Test
    void markNotificationAsRead_ShouldReturnNotFoundWhenNotificationDoesNotExist() throws Exception {
        // Given
        Long notificationId = 999L;
        doThrow(new ResourceNotFoundException("Notification","id",notificationId)).when(notificationService).markNotificationAsRead(notificationId);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/notifications/read/{notificationId}", notificationId))
                .andExpect(status().isNotFound());

        verify(notificationService, times(1)).markNotificationAsRead(notificationId);
    }

    @Test
    void getNotifications_ShouldReturnListOfNotifications() throws Exception {
        // Given
        NotificationDto notification1 = new NotificationDto("Test message 1", null, null);
        NotificationDto notification2 = new NotificationDto("Test message 2", null, null);
        List<NotificationDto> notifications = Arrays.asList(notification1, notification2);

        when(notificationService.getAllNotifications(1)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/notifications/all/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].message").value("Test message 1"))
                .andExpect(jsonPath("$[1].message").value("Test message 2"));

        verify(notificationService, times(1)).getAllNotifications(1);
    }
}
