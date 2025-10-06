package com.hanguyen.demo_spring_bai1.dto.response;

import com.hanguyen.demo_spring_bai1.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String access_token;
    String refresh_token;
    User user;
    boolean authenticated;
}
