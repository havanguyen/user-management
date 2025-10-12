package com.hanguyen.demo_spring_bai1.controller;

import com.hanguyen.demo_spring_bai1.dto.request.ApiResponse;
import com.hanguyen.demo_spring_bai1.dto.response.MyScheduleResponse;
import com.hanguyen.demo_spring_bai1.entity.Course;
import com.hanguyen.demo_spring_bai1.entity.Enrollment;
import com.hanguyen.demo_spring_bai1.service.RegistrationCommandService;
import com.hanguyen.demo_spring_bai1.service.StudentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class StudentController {

      StudentService studentService;
      RegistrationCommandService registrationCommandService;

    @GetMapping("/courses/open-for-registration")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<Course>> getOpenCourses() {
        List<Course> openCourses = studentService.getOpenCoursesForRegistration();
        return ApiResponse.ok("Open courses fetched successfully", openCourses);
    }

    @GetMapping("/{studentId}/schedule")
    @PreAuthorize("hasRole('STUDENT') and authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    public ApiResponse<MyScheduleResponse> getMySchedule(
            @PathVariable String studentId,
            @RequestParam String semesterId
    ) {
        MyScheduleResponse schedule = studentService.getMySchedule(studentId, semesterId);
        return ApiResponse.ok("Schedule fetched successfully", schedule);
    }

    @PostMapping("/{studentId}/enrollments")
    @PreAuthorize("hasRole('STUDENT') and authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    public ApiResponse<Enrollment> registerCourse(
            @PathVariable String studentId,
            @RequestParam String courseId
    ) {
        Enrollment enrollment = registrationCommandService.registerCourse(studentId, courseId);
        return ApiResponse.created("Course registered successfully", enrollment);
    }

    @DeleteMapping("/{studentId}/enrollments/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT') and authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    public ApiResponse<Void> dropCourse(
            @PathVariable String studentId,
            @PathVariable String enrollmentId
    ) {
        registrationCommandService.dropCourse(studentId, enrollmentId);
        return ApiResponse.ok("Course dropped successfully", null);
    }
}