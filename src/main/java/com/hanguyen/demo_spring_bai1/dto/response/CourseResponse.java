package com.hanguyen.demo_spring_bai1.dto.response;

import com.hanguyen.demo_spring_bai1.entity.Lecturer;
import com.hanguyen.demo_spring_bai1.entity.Semester;
import com.hanguyen.demo_spring_bai1.entity.Subject;
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
    String status;

    Subject subject;
    Semester semester;
    UserResponse lecturer;
}