package com.hanguyen.demo_spring_bai1.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentInCourseResponse {
    private String enrollmentId;
    private String studentCode;
    private String studentFullName;
    private Double grade;
}