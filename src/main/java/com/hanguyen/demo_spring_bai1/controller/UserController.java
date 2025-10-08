package com.hanguyen.demo_spring_bai1.controller;

import com.hanguyen.demo_spring_bai1.dto.request.ApiResponse;
import com.hanguyen.demo_spring_bai1.dto.request.UserCreationRequest;
import com.hanguyen.demo_spring_bai1.dto.request.UserUpdateRequest;
import com.hanguyen.demo_spring_bai1.dto.response.UserResponse;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserCreationRequest request) {
        User created = userService.createUser(request);
        return ApiResponse.created("User created successfully", created);
    }

    @GetMapping
    public List<User> getAllUsers() {

       var auth = SecurityContextHolder.getContext().getAuthentication();


        return userService.getAllUser();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }
    @GetMapping("/explicit")
    public ApiResponse<List<User>> getUsersExplicit() {
        List<User> users = userService.getAllUser();
        return ApiResponse.ok("Custom message", users);
    }
}