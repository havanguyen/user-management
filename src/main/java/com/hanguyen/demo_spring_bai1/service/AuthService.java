package com.hanguyen.demo_spring_bai1.service;

import com.hanguyen.demo_spring_bai1.dto.request.AuthRequest;
import com.hanguyen.demo_spring_bai1.dto.request.RegisterRequest;
import com.hanguyen.demo_spring_bai1.dto.response.AuthResponse;
import com.hanguyen.demo_spring_bai1.entity.Lecturer;
import com.hanguyen.demo_spring_bai1.entity.Student;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.enums.Roles;
import com.hanguyen.demo_spring_bai1.exception.BusinessException;
import com.hanguyen.demo_spring_bai1.repository.LecturerRepository;
import com.hanguyen.demo_spring_bai1.repository.StudentRepository;
import com.hanguyen.demo_spring_bai1.repository.UserRepository;
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

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtTokenProvider.generateToken(user.getUsername(), user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername(), user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

            return AuthResponse.builder()
                    .authenticated(true)
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .user(user)
                    .build();

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
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
                Student studentProfile = Student.builder()
                        .user(savedUser)
                        .studentCode(request.getStudentCode())
                        .enrollmentYear(request.getEnrollmentYear())
                        .build();
                studentRepository.save(studentProfile);
                break;
            case LECTURER:
                Lecturer lecturerProfile = Lecturer.builder()
                        .user(savedUser)
                        .lecturerCode(request.getLecturerCode())
                        .degree(request.getDegree())
                        .build();
                lecturerRepository.save(lecturerProfile);
                break;
            case ADMIN:
                break;
            default:
                throw new BusinessException("Invalid role for registration.");
        }

        String accessToken = jwtTokenProvider.generateToken(savedUser.getUsername(), savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getUsername(), savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

        return AuthResponse.builder()
                .authenticated(true)
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .user(savedUser)
                .build();
    }
}