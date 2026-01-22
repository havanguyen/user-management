package com.hanguyen.registercourses.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenProvider {

    JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        try {
            return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
        } catch (Exception e) {
            log.error("Error creating signing key", e);
            throw new RuntimeException("JWT configuration error", e);
        }
    }

    public String generateToken(String username, Set<String> roles) {
        return generateTokenWithExpiration(username, roles, jwtProperties.getAccessTokenValidity());
    }

    public String generateRefreshToken(String username, Set<String> roles) {
        return generateTokenWithExpiration(username, roles, jwtProperties.getRefreshTokenValidity());
    }

    private String generateTokenWithExpiration(String username, Set<String> roles, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .claim("scope", buildScope(roles))
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
            throw new RuntimeException("Invalid JWT token", e);
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

    private String buildScope(Set<String> roles) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        roles.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }
}
