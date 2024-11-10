package com.incture.taskmanagement.controllers;

import com.incture.taskmanagement.payloads.ApiResponse;
import com.incture.taskmanagement.payloads.UserDto;
import com.incture.taskmanagement.services.UserService;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        // Initialize mock data for the test
        userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("Virat Kohli");
        userDto.setEmail("viratkohli0511@gmail.com");
        userDto.setAbout("I live in Bengaluru");
    }

    @Test
    void createUser_ShouldReturnUserDto() {
        // Mock the service call
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        // Make the API call
        ResponseEntity<UserDto> response = userController.createUser(userDto);

        // Assert the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Virat Kohli", response.getBody().getName());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDto() {
        Integer userId = 1;
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1);
        updatedUserDto.setName("Updated Name");
        updatedUserDto.setEmail("updatedemail@example.com");

        // Mock the service call
        when(userService.updateUser(any(UserDto.class), eq(userId))).thenReturn(updatedUserDto);

        // Make the API call
        ResponseEntity<UserDto> response = userController.updateUser(updatedUserDto, userId);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getName());
    }

    @Test
    void deleteUser_ShouldReturnApiResponse() {
        Integer userId = 1;

        // Mock the service call
        doNothing().when(userService).deleteUser(userId);

        // Make the API call
        ResponseEntity<ApiResponse> response = userController.deleteUser(userId);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Deleted Successfully", response.getBody().getMessage());
    }

    @Test
    void getSingleUser_ShouldReturnUserDto() {
        Integer userId = 1;

        // Mock the service call
        when(userService.getUserById(userId)).thenReturn(userDto);

        // Make the API call
        ResponseEntity<UserDto> response = userController.getSingleUser(userId);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Virat Kohli", response.getBody().getName());
    }

    @Test
    void getUserByEmail_ShouldReturnUserDto() {
        String email = "viratkohli0511@gmail.com";

        // Mock the service call
        when(userService.getUserByEmail(email)).thenReturn(userDto);

        // Make the API call
        ResponseEntity<UserDto> response = userController.getUserByEmail(email);

        // Assert the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Virat Kohli", response.getBody().getName());
    }
}
