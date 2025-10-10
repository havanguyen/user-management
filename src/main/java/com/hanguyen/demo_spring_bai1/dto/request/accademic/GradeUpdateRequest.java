package com.hanguyen.demo_spring_bai1.dto.request.accademic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeUpdateRequest {
    @NotBlank(message = "Enrollment ID is required")
    private String enrollmentId;

    @NotNull(message = "Grade is required")
    @Min(value = 0, message = "Grade must be between 0 and 10")
    @Max(value = 10, message = "Grade must be between 0 and 10")
    private Double grade;
}