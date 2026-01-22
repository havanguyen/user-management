package com.hanguyen.registercourses.dto.request;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationPeriodRequest {
    @NotBlank(message = "SEMESTER_ID_REQUIRED")
    String semesterId;
    @NotNull(message = "START_TIME_REQUIRED")
    LocalDateTime startTime;
    @NotNull(message = "END_TIME_REQUIRED")
    @Future(message = "END_TIME_FUTURE")
    LocalDateTime endTime;
}