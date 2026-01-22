package com.hanguyen.registercourses.controller;
import com.hanguyen.registercourses.dto.request.UserCreationRequest;
import com.hanguyen.registercourses.dto.request.UserUpdateRequest;
import com.hanguyen.registercourses.common.ApiResponse;
import com.hanguyen.registercourses.common.PageResponse;
import com.hanguyen.registercourses.constant.SuccessCode;
import com.hanguyen.registercourses.dto.response.UserResponse;
import com.hanguyen.registercourses.entity.User;
import com.hanguyen.registercourses.service.registration.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    @PostMapping
    public ApiResponse<User> createUser(@Valid @RequestBody UserCreationRequest request) {
        User created = userService.createUser(request);
        return ApiResponse.buildSuccessResponse(created, SuccessCode.CREATE_USER_SUCCESSFUL);
    }
    @GetMapping
    public ApiResponse<PageResponse<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy) {
        return ApiResponse.buildSuccessResponse(
                PageResponse.fromPage(userService.getAllUser(page, size, sortBy)),
                SuccessCode.GET_ALL_USERS_SUCCESSFUL);
    }
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        return ApiResponse.buildSuccessResponse(userService.getUser(userId), SuccessCode.GET_USER_SUCCESSFUL);
    }
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.buildSuccessResponse(userService.updateUser(userId, request),
                SuccessCode.UPDATE_USER_SUCCESSFUL);
    }
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.buildSuccessResponse(null, SuccessCode.DELETE_USER_SUCCESSFUL);
    }
}