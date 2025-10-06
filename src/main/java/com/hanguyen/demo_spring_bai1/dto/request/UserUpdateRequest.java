package com.hanguyen.demo_spring_bai1.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    String password;

    @Size(max = 50, message = "First name must be less than 50 characters")
    String firstname;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    String lastname;

    @Past(message = "Date of birth must be in the past")
    LocalDate dod;
}
