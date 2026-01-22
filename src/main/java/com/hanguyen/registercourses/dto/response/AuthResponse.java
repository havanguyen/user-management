package com.hanguyen.registercourses.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hanguyen.registercourses.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    @JsonIgnore
    String accessToken;

    @JsonIgnore
    String refreshToken;

    User user;
    boolean authenticated;

    public void clearTokens() {
        this.accessToken = null;
        this.refreshToken = null;
    }
}
