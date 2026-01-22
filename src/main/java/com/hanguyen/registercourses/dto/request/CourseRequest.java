package com.hanguyen.registercourses.dto.request;
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
    @NotBlank(message = "COURSE_CODE_REQUIRED")
    String courseCode;
    @NotNull(message = "MAX_STUDENTS_MIN")
    @Min(value = 1, message = "MAX_STUDENTS_MIN")
    Integer maxStudents;
    @NotBlank(message = "SCHEDULE_INFO_REQUIRED")
    String scheduleInfo;
    @NotBlank(message = "SUBJECT_ID_REQUIRED")
    String subjectId;
    @NotBlank(message = "SEMESTER_ID_REQUIRED")
    String semesterId;
    @NotBlank(message = "LECTURER_ID_REQUIRED")
    String lecturerId;
}