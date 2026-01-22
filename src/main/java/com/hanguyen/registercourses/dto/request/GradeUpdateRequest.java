package com.hanguyen.registercourses.dto.request;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class GradeUpdateRequest {
    @NotBlank(message = "ENROLLMENT_ID_REQUIRED")
    private String enrollmentId;
    @NotNull(message = "GRADE_REQUIRED")
    @Min(value = 0, message = "GRADE_RANGE")
    @Max(value = 10, message = "GRADE_RANGE")
    private Double grade;
}