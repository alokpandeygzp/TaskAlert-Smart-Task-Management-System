package com.incture.taskmanagement.controllers;

import com.incture.taskmanagement.entities.User;
import com.incture.taskmanagement.exceptions.ApiException;
import com.incture.taskmanagement.payloads.ApiResponse;
import com.incture.taskmanagement.payloads.JwtAuthRequest;
import com.incture.taskmanagement.payloads.JwtAuthResponse;
import com.incture.taskmanagement.payloads.UserDto;
import com.incture.taskmanagement.repositories.UserRepo;
import com.incture.taskmanagement.security.JwtTokenHelper;
import com.incture.taskmanagement.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth/")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final JwtTokenHelper jwtTokenHelper;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtTokenHelper jwtTokenHelper, UserDetailsService userDetailsService, UserService userService, UserRepo userRepo, AuthenticationManager authenticationManager) {
        this.jwtTokenHelper = jwtTokenHelper;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> createToken(
            @RequestBody JwtAuthRequest request
            ) {

        this.authenticate(request.getUsername(), request.getPassword());

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());
        String generatedToken = this.jwtTokenHelper.generateToken(userDetails);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(generatedToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void authenticate(String username, String password) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(username, password);

        try {
            this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (BadCredentialsException ex) {
            System.out.println("Invalid Details !");
            throw new ApiException("Invalid username or password !!");
        }
    }


    //register new user api
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        // Check if the email already exists
        Optional<User> existingUser = this.userRepo.findByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            // Return a response indicating the email is already taken
            return new ResponseEntity<>(new ApiResponse("Email already exists", false), HttpStatus.BAD_REQUEST);
        }

        // If the email is available, proceed to register the new user
        UserDto registeredUser = this.userService.registerNewUser(userDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
}
