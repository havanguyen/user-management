package com.hanguyen.demo_spring_bai1.service;

import com.hanguyen.demo_spring_bai1.entity.RefreshToken;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.exception.BusinessException;
import com.hanguyen.demo_spring_bai1.repository.RefreshTokenRepository;
import com.hanguyen.demo_spring_bai1.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();
        return refreshTokenRepository.save(newRefreshToken);
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new BusinessException("Refresh token was expired. Please make a new signin request");
        }
    }

    @Transactional
    public int deleteByUserId(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(refreshTokenRepository::deleteByUser).orElse(0);
    }
}