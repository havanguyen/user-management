package com.hanguyen.demo_spring_bai1.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey;
    private long accessTokenValidity = 3600000;
    private long refreshTokenValidity = 604800000;
}
