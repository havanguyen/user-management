package com.hanguyen.demo_spring_bai1.controller;

import com.hanguyen.demo_spring_bai1.dto.request.ApiResponse;
import com.hanguyen.demo_spring_bai1.dto.response.MyScheduleResponse;
import com.hanguyen.demo_spring_bai1.entity.Course;
import com.hanguyen.demo_spring_bai1.entity.Enrollment;
import com.hanguyen.demo_spring_bai1.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/courses/open-for-registration")
    public ApiResponse<List<Course>> getOpenCourses() {
        List<Course> openCourses = studentService.getOpenCoursesForRegistration();
        return ApiResponse.ok("Open courses fetched successfully", openCourses);
    }

    @PreAuthorize("authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    @GetMapping("/{studentId}/schedule")
    public ApiResponse<MyScheduleResponse> getMySchedule(
            @PathVariable String studentId,
            @RequestParam String semesterId
    ) {
        MyScheduleResponse schedule = studentService.getMySchedule(studentId, semesterId);
        return ApiResponse.ok("Schedule fetched successfully", schedule);
    }

    @PreAuthorize("authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    @PostMapping("/{studentId}/enrollments")
    public ApiResponse<Enrollment> registerCourse(
            @PathVariable String studentId,
            @RequestParam String courseId
    ) {
        Enrollment enrollment = studentService.registerCourse(studentId, courseId);
        return ApiResponse.created("Course registered successfully", enrollment);
    }

    @PreAuthorize("authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    @DeleteMapping("/{studentId}/enrollments/{enrollmentId}")
    public ApiResponse<Void> dropCourse(
            @PathVariable String studentId,
            @PathVariable String enrollmentId
    ) {
        studentService.dropCourse(studentId, enrollmentId);
        return ApiResponse.ok("Course dropped successfully", null);
    }
}