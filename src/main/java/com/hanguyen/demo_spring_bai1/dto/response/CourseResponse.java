package com.hanguyen.demo_spring_bai1.dto.response;

import com.hanguyen.demo_spring_bai1.enums.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String id;
    String courseCode;
    int maxStudents;
    int currentStudents;
    String scheduleInfo;
    CourseStatus status;
    SubjectResponse subject;
    SemesterResponse semester;
    LecturerResponse lecturer;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectResponse {
        String subjectCode;
        String name;
        int credits;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SemesterResponse {
        String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LecturerResponse {
        String fullName;
        String degree;
    }
}