package com.hanguyen.registercourses.service.registration;

import com.hanguyen.registercourses.entity.RefreshToken;
import com.hanguyen.registercourses.entity.User;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.repository.RefreshTokenRepository;
import com.hanguyen.registercourses.repository.UserRepository;
import com.hanguyen.registercourses.exception.AppException;
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
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

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
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
    }

    @Transactional
    public int deleteByUserId(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(refreshTokenRepository::deleteByUser).orElse(0);
    }
}