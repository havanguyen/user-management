package com.hanguyen.demo_spring_bai1.controller;

import com.hanguyen.demo_spring_bai1.dto.request.UserCreationRequest;
import com.hanguyen.demo_spring_bai1.dto.request.UserUpdateRequest;
import com.hanguyen.demo_spring_bai1.dto.response.ApiResponse;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserCreationRequest request) {
        User created = userService.createUser(request);
        return ApiResponse.created("User created successfully", created);
    }

    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        return ApiResponse.ok("Get all users successfully", userService.getAllUser());
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        return ApiResponse.ok("Get user successfully", userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.ok("User updated successfully", userService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.ok("User deleted successfully", null);
    }

    @GetMapping("/explicit")
    public ApiResponse<List<User>> getUsersExplicit() {
        List<User> users = userService.getAllUser();
        return ApiResponse.ok("Custom message", users);
    }
}