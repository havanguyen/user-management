package com.hanguyen.registercourses.controller;
import com.hanguyen.registercourses.common.CookieUtils;
import com.hanguyen.registercourses.dto.request.AuthRequest;
import com.hanguyen.registercourses.dto.request.RefreshTokenRequest;
import com.hanguyen.registercourses.dto.request.RegisterRequest;
import com.hanguyen.registercourses.dto.response.AuthResponse;
import com.hanguyen.registercourses.common.ApiResponse;
import com.hanguyen.registercourses.constant.SuccessCode;
import com.hanguyen.registercourses.service.auth.AuthService;
import com.hanguyen.registercourses.service.registration.RegistrationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    CookieUtils cookieUtils;
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid AuthRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        cookieUtils.setCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());
        authResponse.clearTokens();
        return ApiResponse.buildSuccessResponse(authResponse, SuccessCode.LOGIN_SUCCESSFUL);
    }
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = registrationService.registerUser(request);
        cookieUtils.setCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());
        authResponse.clearTokens();
        return ApiResponse.buildSuccessResponse(authResponse, SuccessCode.REGISTER_SUCCESSFUL);
    }
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        cookieUtils.setAccessCookie(response, authResponse.getAccessToken());
        authResponse.clearTokens();
        return ApiResponse.buildSuccessResponse(authResponse, SuccessCode.REFRESH_TOKEN_SUCCESSFUL);
    }
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        cookieUtils.clearCookies(response);
        return ApiResponse.buildSuccessResponse(null, SuccessCode.LOGOUT_SUCCESSFUL);
    }
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("REFRESH_TOKEN".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}