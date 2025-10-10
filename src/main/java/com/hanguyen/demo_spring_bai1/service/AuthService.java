package com.hanguyen.demo_spring_bai1.service;

import com.hanguyen.demo_spring_bai1.dto.request.AuthRequest;
import com.hanguyen.demo_spring_bai1.dto.request.RefreshTokenRequest;
import com.hanguyen.demo_spring_bai1.dto.request.RegisterRequest;
import com.hanguyen.demo_spring_bai1.dto.response.AuthResponse;
import com.hanguyen.demo_spring_bai1.entity.*;
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
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final MajorRepository majorRepository;
    private final DepartmentRepository departmentRepository;
    private final RefreshTokenService refreshTokenService; // Đã thêm
    private final RefreshTokenRepository refreshTokenRepository; // Đã thêm

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

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
            throw new RuntimeException("Invalid username or password");
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Refresh token not found"));

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
                .orElseThrow(() -> new BusinessException("Refresh token not found"));
        refreshTokenRepository.delete(refreshToken);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .dod(request.getDod())
                .roles(new HashSet<>())
                .build();

        user.getRoles().add(request.getRole());
        User savedUser = userRepository.save(user);

        switch (request.getRole()) {
            case STUDENT:
                if (request.getMajorId() == null) {
                    throw new BusinessException("Major ID is required for student registration.");
                }
                Major major = majorRepository.findById(request.getMajorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Major", "id", request.getMajorId()));

                Student studentProfile = Student.builder()
                        .user(savedUser)
                        .studentCode(request.getStudentCode())
                        .enrollmentYear(request.getEnrollmentYear())
                        .major(major)
                        .build();
                studentRepository.save(studentProfile);
                break;

            case LECTURER:
                if (request.getDepartmentId() == null) {
                    throw new BusinessException("Department ID is required for lecturer registration.");
                }
                Department department = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

                Lecturer lecturerProfile = Lecturer.builder()
                        .user(savedUser)
                        .lecturerCode(request.getLecturerCode())
                        .degree(request.getDegree())
                        .department(department)
                        .build();
                lecturerRepository.save(lecturerProfile);
                break;

            case ADMIN:
                break;
            default:
                throw new BusinessException("Invalid role for registration.");
        }

        String accessToken = jwtTokenProvider.generateToken(savedUser.getUsername(), savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getUsername());

        return AuthResponse.builder()
                .authenticated(true)
                .access_token(accessToken)
                .refresh_token(refreshToken.getToken())
                .user(savedUser)
                .build();
    }
}