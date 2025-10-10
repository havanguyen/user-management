package com.hanguyen.demo_spring_bai1.dto.request.accademic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequest {
    @NotBlank(message = "Course code is required")
    String courseCode;

    @NotNull
    @Min(value = 1, message = "Max students must be at least 1")
    Integer maxStudents;

    @NotBlank(message = "Schedule info is required")
    String scheduleInfo;

    @NotBlank(message = "Subject ID is required")
    String subjectId;

    @NotBlank(message = "Semester ID is required")
    String semesterId;

    @NotBlank(message = "Lecturer ID is required")
    String lecturerId;
}