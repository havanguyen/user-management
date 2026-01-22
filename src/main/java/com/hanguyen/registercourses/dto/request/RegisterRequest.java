package com.hanguyen.registercourses.dto.request;
import com.hanguyen.registercourses.constant.Roles;
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
    @NotBlank(message = "USERNAME_BLANK")
    @Size(min = 3, max = 20, message = "USERNAME_SIZE")
    String username;
    @NotBlank(message = "PASSWORD_BLANK")
    @Size(min = 8, max = 100, message = "PASSWORD_SIZE")
    String password;
    @NotBlank(message = "FIRSTNAME_BLANK")
    String firstname;
    @NotBlank(message = "LASTNAME_BLANK")
    String lastname;
    LocalDate dod;
    @NotNull(message = "ROLE_REQUIRED")
    Roles role;
    String studentCode;
    Integer enrollmentYear;
    String majorId;
    String lecturerCode;
    String degree;
    String departmentId;
}