package com.hanguyen.registercourses.dto.request;

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
    @NotBlank(message = "SUBJECT_CODE_REQUIRED")
    String subjectCode;

    @NotBlank(message = "SUBJECT_NAME_REQUIRED")
    String name;

    @NotNull(message = "CREDITS_MIN")
    @Min(value = 1, message = "CREDITS_MIN")
    Integer credits;

    @NotBlank(message = "DEPARTMENT_ID_REQUIRED_FIELD")
    String departmentId;

    Set<String> prerequisiteIds;
}