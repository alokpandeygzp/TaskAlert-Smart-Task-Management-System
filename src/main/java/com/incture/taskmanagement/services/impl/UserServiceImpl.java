package com.incture.taskmanagement.services.impl;

import com.incture.taskmanagement.config.AppConstants;
import com.incture.taskmanagement.entities.Role;
import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.exceptions.ResourceNotFoundException;
import com.incture.taskmanagement.payloads.UserDto;
import com.incture.taskmanagement.repositories.RoleRepo;
import com.incture.taskmanagement.repositories.TaskRepository;
import com.incture.taskmanagement.repositories.UserRepo;
import com.incture.taskmanagement.services.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo, TaskRepository taskRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto registerNewUser(UserDto userDto) {

        try {
            User user = this.modelMapper.map(userDto, User.class);
            //Set encoded password
            user.setPassword(this.passwordEncoder.encode(user.getPassword()));

            //Set roles -> New user has normal role only
            Role role = this.roleRepo.findById(AppConstants.NORMAL_USER).get();
            user.getRoles().add(role);

            User newUser = this.userRepo.save(user);
            logger.info("New user created with ID {}", newUser.getId());
            return this.modelMapper.map(newUser, UserDto.class);
        }
        catch (Exception e) {
            logger.error("Error occurred during user registration: {}", e.getMessage(), e);
            throw new RuntimeException("Error during user registration", e);
        }
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            User user = this.dtoToUser(userDto);
            User savedUser = this.userRepo.save(user);
            logger.info("User created with ID {}", savedUser.getId());

            return this.userToDto(savedUser);
        } catch (Exception e) {
            logger.error("Error occurred while creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        try {
            User user = this.userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
            user.setAbout(userDto.getAbout());

            User updatedUser = this.userRepo.save(user);
            logger.info("User with ID {} updated", updatedUser.getId());
            return this.userToDto(updatedUser);
        }
        catch (Exception e) {
            logger.error("Error occurred while updating user with ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error updating user", e);
        }
    }

    @Override
    public UserDto getUserById(Integer userId) {
        try {
            User user = this.userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
            return this.userToDto(user);
        }
        catch (Exception e) {
            logger.error("Error occurred while fetching user with ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error fetching user", e);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        try {
            List<User> users = this.userRepo.findAll();
            logger.info("Fetched all users. Total users: {}", users.size());
            List<UserDto> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());
            return userDtos;
        }
        catch (Exception e) {
            logger.error("Error occurred while fetching all users: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching users", e);
        }
    }

    @Override
    @Transactional // Ensure that the method is run within a transaction context
    public void deleteUser(Integer userId) {
        try {
            // Find the user by ID
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Remove the user-role relationship (not delete roles themselves)
                user.getRoles().clear();

                // Save the user after clearing the roles (optional if you want to maintain this in DB)
                userRepo.save(user);

                // Custom query to remove all tasks for this user
                taskRepository.deleteAllByAssignedUserId(userId);

                // Now delete the user itself
                userRepo.delete(user);
                logger.info("User with ID {} deleted", userId);
            } else {
                logger.error("User with ID {} not found for deletion", userId);
                throw new ResourceNotFoundException("User", "id", userId);
            }
        }
        catch (Exception e) {
            logger.error("Error occurred while deleting user with ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public UserDto getUserByEmail(String email) {
        try {
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "Email : " + email, 0));
            return this.modelMapper.map(user, UserDto.class);
        }
        catch (Exception e) {
            logger.error("Error occurred while fetching user with email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Error fetching user by email", e);
        }
    }

    public User dtoToUser(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

    public UserDto userToDto(User user) {
        return this.modelMapper.map(user, UserDto.class);
    }
}
