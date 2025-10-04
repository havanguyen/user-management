package com.hanguyen.demo_spring_bai1.controller;

import com.hanguyen.demo_spring_bai1.dto.request.ApiResponse;
import com.hanguyen.demo_spring_bai1.dto.request.UserCreationRequest;
import com.hanguyen.demo_spring_bai1.dto.request.UserUpdateRequest;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserCreationRequest request) {
        User created = userService.createUser(request);

        ApiResponse<User> response = new ApiResponse<>();
        response.setStatus(HttpStatus.CREATED.value());
        response.setSuccess(true);
        response.setMessage("User created successfully");
        response.setData(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUser();

        ApiResponse<List<User>> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setSuccess(true);
        response.setMessage("Fetched all users successfully");
        response.setData(users);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable String userId) {
        User user = userService.getUser(userId);

        ApiResponse<User> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setSuccess(true);
        response.setMessage("User retrieved successfully");
        response.setData(user);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        User updated = userService.updateUser(userId, request);

        ApiResponse<User> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setSuccess(true);
        response.setMessage("User updated successfully");
        response.setData(updated);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK.value());
        response.setSuccess(true);
        response.setMessage("User deleted successfully");

        return ResponseEntity.ok(response);
    }
}
