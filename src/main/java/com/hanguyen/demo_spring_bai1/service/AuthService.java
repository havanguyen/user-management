package com.hanguyen.demo_spring_bai1.service;

import com.hanguyen.demo_spring_bai1.dto.request.AuthRequest;
import com.hanguyen.demo_spring_bai1.dto.request.RefreshTokenRequest;
import com.hanguyen.demo_spring_bai1.dto.request.RegisterRequest;
import com.hanguyen.demo_spring_bai1.dto.response.AuthResponse;
import com.hanguyen.demo_spring_bai1.entity.*;
import com.hanguyen.demo_spring_bai1.enums.ErrorCode;
import com.hanguyen.demo_spring_bai1.exception.BusinessException;
import com.hanguyen.demo_spring_bai1.exception.ResourceNotFoundException;
import com.hanguyen.demo_spring_bai1.repository.*;
import com.hanguyen.demo_spring_bai1.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            String accessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

            // Tạo refresh token bằng RefreshTokenService
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

            return AuthResponse.builder()
                    .authenticated(true)
                    .access_token(accessToken)
                    .refresh_token(refreshToken.getToken()) // Lấy token string
                    .user(user)
                    .build();

        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user.getUsername(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

        return AuthResponse.builder()
                .authenticated(true)
                .access_token(newAccessToken)
                .refresh_token(refreshToken.getToken())
                .user(user)
                .build();
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        refreshTokenRepository.delete(refreshToken);
    }
}