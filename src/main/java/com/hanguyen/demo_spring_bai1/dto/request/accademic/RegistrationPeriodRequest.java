package com.hanguyen.demo_spring_bai1.dto.request.accademic;

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
    @NotBlank(message = "Semester ID is required")
    String semesterId;

    @NotNull(message = "Start time is required")
    LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    LocalDateTime endTime;
}