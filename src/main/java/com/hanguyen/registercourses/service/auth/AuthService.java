package com.hanguyen.registercourses.service.auth;

import com.hanguyen.registercourses.dto.request.AuthRequest;
import com.hanguyen.registercourses.dto.response.AuthResponse;
import com.hanguyen.registercourses.entity.*;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.repository.*;
import com.hanguyen.registercourses.security.JwtTokenProvider;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.service.registration.RefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

        AuthenticationManager authenticationManager;
        JwtTokenProvider jwtTokenProvider;
        UserRepository userRepository;
        RefreshTokenService refreshTokenService;
        RefreshTokenRepository refreshTokenRepository;

        public AuthResponse login(AuthRequest request) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getUsername(), request.getPassword()));

                        User user = userRepository.findByUsername(request.getUsername())
                                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                        String accessToken = jwtTokenProvider.generateToken(user.getUsername(),
                                        user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

                        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

                        return AuthResponse.builder()
                                        .authenticated(true)
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken.getToken())
                                        .user(user)
                                        .build();

                } catch (AuthenticationException e) {
                        throw new AppException(ErrorCode.INVALID_CREDENTIALS);
                }
        }

        public AuthResponse refreshToken(String token) {
                if (token == null) {
                        throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
                }

                RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

                refreshTokenService.verifyExpiration(refreshToken);

                User user = refreshToken.getUser();
                String newAccessToken = jwtTokenProvider.generateToken(user.getUsername(),
                                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

                return AuthResponse.builder()
                                .authenticated(true)
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken.getToken())
                                .user(user)
                                .build();
        }

        @Transactional
        public void logout(String token) {
                if (token == null) {
                        return;
                }
                refreshTokenRepository.findByToken(token)
                                .ifPresent(refreshTokenRepository::delete);
        }
}