package com.hanguyen.registercourses.dto.request;
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
    @Size(min = 8, max = 100, message = "PASSWORD_SIZE")
    String password;
    @Size(max = 50, message = "FIRSTNAME_SIZE")
    String firstname;
    @Size(max = 50, message = "LASTNAME_SIZE")
    String lastname;
    @Past(message = "DOB_PAST")
    LocalDate dod;
}
