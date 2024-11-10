package com.incture.taskmanagement.services;

import com.incture.taskmanagement.config.AppConstants;
import com.incture.taskmanagement.entities.Role;
import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.exceptions.ResourceNotFoundException;
import com.incture.taskmanagement.payloads.UserDto;
import com.incture.taskmanagement.repositories.RoleRepo;
import com.incture.taskmanagement.repositories.TaskRepository;
import com.incture.taskmanagement.repositories.UserRepo;
import com.incture.taskmanagement.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private Role role;

    @BeforeEach
    void setUp() {
        // Setup sample userDto and user objects
        userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("Test User");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword("encodedpassword");
        userDto.setAbout("Test About");

        user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("testuser@example.com");
        user.setPassword("encodedpassword");
        user.setAbout("Test About");

        // Role setup
        role = new Role();
        role.setId(Long.valueOf(AppConstants.NORMAL_USER));
        role.setName("NORMAL_USER");
    }


    @Test
    void testRegisterNewUser() {
        // Mock dependencies
        when(roleRepo.findById(AppConstants.NORMAL_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        // Call the service method
        UserDto result = userService.registerNewUser(userDto);

        // Verify the interactions and results
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("testuser@example.com", result.getEmail());
        assertEquals("encodedpassword", result.getPassword());
        assertEquals("Test About", result.getAbout());

        verify(roleRepo, times(1)).findById(AppConstants.NORMAL_USER);
        verify(passwordEncoder, times(1)).encode(userDto.getPassword());
        verify(userRepo, times(1)).save(user);
        verify(modelMapper, times(1)).map(user, UserDto.class);
    }

    @Test
    void testCreateUser() {
        // Prepare mock behavior
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        // Call the method
        UserDto result = userService.createUser(userDto);

        // Verify the results
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("testuser@example.com", result.getEmail());
        verify(userRepo, times(1)).save(user);
    }


    // Test for updating an existing user
    @Test
    void updateUser_shouldReturnUpdatedUserDto_whenUserExists() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.updateUser(userDto, 1);

        // Assert
        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepo).findById(1); // Ensure findById is called
        verify(userRepo).save(any(User.class)); // Ensure save is called
    }

    // Test for user update when user is not found
    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userDto, 1));
    }

    // Test for getting a user by ID
    @Test
    void getUserById_shouldReturnUserDto_whenUserExists() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.getUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepo).findById(1); // Ensure findById is called
    }

    // Test for getting a user by ID when user does not exist
    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
    }

    // Test for getting all users
    @Test
    void getAllUsers_shouldReturnListOfUserDtos_whenUsersExist() {
        // Arrange
        when(userRepo.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        // Act
        List<UserDto> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getName(), result.get(0).getName());
        assertEquals(userDto.getEmail(), result.get(0).getEmail());
        verify(userRepo).findAll(); // Ensure findAll is called
    }

    // Test for deleting a user
    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(taskRepository).deleteAllByAssignedUserId(1);
        doNothing().when(userRepo).delete(any(User.class));

        // Act
        userService.deleteUser(1);

        // Assert
        verify(userRepo).findById(1); // Ensure findById is called
        verify(taskRepository).deleteAllByAssignedUserId(1); // Ensure tasks are deleted
        verify(userRepo).delete(any(User.class)); // Ensure user is deleted
    }

    // Test for deleting a user when user is not found
    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1));
    }

    // Test for getting a user by email
    @Test
    void getUserByEmail_shouldReturnUserDto_whenUserExists() {
        // Arrange
        when(userRepo.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserDto.class))).thenReturn(userDto);

        // Act
        UserDto result = userService.getUserByEmail("john.doe@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepo).findByEmail("john.doe@example.com"); // Ensure findByEmail is called
    }

    // Test for getting a user by email when user does not exist
    @Test
    void getUserByEmail_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepo.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("john.doe@example.com"));
    }
}
