package com.hanguyen.registercourses.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @NotBlank(message = "USERNAME_BLANK")
    @Size(min = 3, max = 20, message = "USERNAME_SIZE")
    String username;
    @NotBlank(message = "PASSWORD_BLANK")
    @Size(min = 8, max = 100, message = "PASSWORD_SIZE")
    String password;
    @NotBlank(message = "FIRSTNAME_BLANK")
    @Size(max = 50, message = "FIRSTNAME_SIZE")
    String firstname;
    @NotBlank(message = "LASTNAME_BLANK")
    @Size(max = 50, message = "LASTNAME_SIZE")
    String lastname;
    @Past(message = "DOB_PAST")
    LocalDate dod;
}
