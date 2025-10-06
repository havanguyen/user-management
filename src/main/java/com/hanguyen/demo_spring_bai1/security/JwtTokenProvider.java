package com.hanguyen.demo_spring_bai1.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String jwtSecret;

    @Value("${jwt.access-token-validity:3600000}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity:604800000}")
    private long refreshTokenValidity;

    private SecretKey getSigningKey() {
        try {
            return Keys.hmacShaKeyFor(jwtSecret.getBytes());
        } catch (Exception e) {
            log.error("Error creating signing key", e);
            throw new RuntimeException("JWT configuration error");
        }
    }

    public String generateToken(String username) {
        return generateTokenWithExpiration(username, accessTokenValidity);
    }

    public String generateRefreshToken(String username) {
        return generateTokenWithExpiration(username, refreshTokenValidity);
    }

    private String generateTokenWithExpiration(String username, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.error("JWT token validation error: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}