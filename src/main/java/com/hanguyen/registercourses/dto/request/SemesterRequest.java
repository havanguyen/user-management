package com.hanguyen.registercourses.dto.request;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterRequest {
    @NotBlank(message = "SEMESTER_NAME_BLANK")
    String name;
    @NotNull(message = "START_DATE_REQUIRED")
    LocalDate startDate;
    @NotNull(message = "END_DATE_REQUIRED")
    @Future(message = "END_DATE_FUTURE")
    LocalDate endDate;
}