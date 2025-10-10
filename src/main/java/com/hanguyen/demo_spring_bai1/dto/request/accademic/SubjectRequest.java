package com.hanguyen.demo_spring_bai1.dto.request.accademic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectRequest {
    @NotBlank(message = "Subject code is required")
    String subjectCode;

    @NotBlank(message = "Subject name is required")
    String name;

    @NotNull
    @Min(value = 1, message = "Credits must be at least 1")
    Integer credits;

    @NotBlank(message = "Department ID is required")
    String departmentId;

    Set<String> prerequisiteIds;
}