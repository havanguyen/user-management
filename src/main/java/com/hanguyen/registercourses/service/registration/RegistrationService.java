package com.hanguyen.registercourses.service.registration;

import com.hanguyen.registercourses.dto.request.RegisterRequest;
import com.hanguyen.registercourses.dto.response.AuthResponse;
import com.hanguyen.registercourses.entity.RefreshToken;
import com.hanguyen.registercourses.entity.User;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.constant.Roles;
import com.hanguyen.registercourses.repository.UserRepository;
import com.hanguyen.registercourses.security.JwtTokenProvider;
import com.hanguyen.registercourses.service.strategy.RegistrationStrategy;
import com.hanguyen.registercourses.exception.AppException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final Map<Roles, RegistrationStrategy> strategies;

    public RegistrationService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService,
            List<RegistrationStrategy> strategyList) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(RegistrationStrategy::supportedRole, Function.identity()));
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
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

        RegistrationStrategy strategy = strategies.get(request.getRole());
        if (strategy != null) {
            strategy.processRegistration(savedUser, request);
        } else if (request.getRole() != Roles.ADMIN) {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }

        String accessToken = jwtTokenProvider.generateToken(
                savedUser.getUsername(),
                savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getUsername());

        return AuthResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(savedUser)
                .build();
    }
}
