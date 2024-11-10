package com.incture.taskmanagement.controllers;

import com.incture.taskmanagement.payloads.ApiResponse;
import com.incture.taskmanagement.payloads.UserDto;
import com.incture.taskmanagement.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createUserDto = this.userService.createUser(userDto);
        return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {

        UserDto updatedUserDto = this.userService.updateUser(userDto, uid);
        return ResponseEntity.ok(updatedUserDto);
    }


    //ADMIN ONLY
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Integer userId) {
        this.userService.deleteUser(userId);
        return new ResponseEntity<>(new ApiResponse("User Deleted Successfully", true), HttpStatus.OK);
    }


    @GetMapping("/")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> userDtoList= this.userService.getAllUsers();
        return ResponseEntity.ok(userDtoList);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getSingleUser(@PathVariable("userId") Integer uid) {
        UserDto userDto = this.userService.getUserById(uid);
        return ResponseEntity.ok(userDto);
    }


    @GetMapping("/email/{user_email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("user_email") String email) {
        UserDto userDto = this.userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }
}
