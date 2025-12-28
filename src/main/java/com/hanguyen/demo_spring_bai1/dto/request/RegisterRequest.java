package com.hanguyen.demo_spring_bai1.dto.request;

import com.hanguyen.demo_spring_bai1.constant.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20)
    String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100)
    String password;

    @NotBlank
    String firstname;

    @NotBlank
    String lastname;

    LocalDate dod;

    @NotNull(message = "Role must be specified")
    Roles role;

    String studentCode;
    Integer enrollmentYear;
    String majorId;

    String lecturerCode;
    String degree;
    String departmentId;
}