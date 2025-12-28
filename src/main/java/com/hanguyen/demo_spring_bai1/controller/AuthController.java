package com.hanguyen.demo_spring_bai1.controller;

import com.hanguyen.demo_spring_bai1.dto.request.AuthRequest;
import com.hanguyen.demo_spring_bai1.dto.request.RefreshTokenRequest;
import com.hanguyen.demo_spring_bai1.dto.request.RegisterRequest;
import com.hanguyen.demo_spring_bai1.dto.response.AuthResponse;
import com.hanguyen.demo_spring_bai1.dto.response.ApiResponse;
import com.hanguyen.demo_spring_bai1.service.AuthService;
import com.hanguyen.demo_spring_bai1.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AuthController {

    AuthService authService;
    RegistrationService registrationService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.ok("Login successful", response);
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registrationService.registerUser(request);
        return ApiResponse.created("User registered successfully", response);
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ApiResponse.ok("Token refreshed successfully", response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ApiResponse.ok("Logged out successfully", null);
    }
}