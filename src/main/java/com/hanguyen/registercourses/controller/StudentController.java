package com.hanguyen.registercourses.controller;

import com.hanguyen.registercourses.dto.response.AsyncRegistrationResponse;
import com.hanguyen.registercourses.dto.response.CourseResponse;
import com.hanguyen.registercourses.common.ApiResponse;
import com.hanguyen.registercourses.common.PageResponse;
import com.hanguyen.registercourses.constant.SuccessCode;
import com.hanguyen.registercourses.dto.response.MyScheduleResponse;
import com.hanguyen.registercourses.entity.Enrollment;
import com.hanguyen.registercourses.messaging.RegistrationMessageProducer;
import com.hanguyen.registercourses.service.registration.RegistrationCommandService;
import com.hanguyen.registercourses.service.registration.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final RegistrationCommandService registrationCommandService;
    private final RegistrationMessageProducer registrationMessageProducer;
    @Value("${registration.async-enabled:false}")
    private boolean asyncEnabled;

    @GetMapping("/courses/open-for-registration")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<PageResponse<CourseResponse>> getOpenCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "courseCode") String orderBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ApiResponse.buildSuccessResponse(
                PageResponse.fromPage(studentService.getOpenCoursesForRegistration(page, size, orderBy, direction)),
                SuccessCode.GET_OPEN_COURSES_SUCCESSFUL);
    }

    @GetMapping("/{studentId}/schedule")
    @PreAuthorize("hasRole('STUDENT') and authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    public ApiResponse<MyScheduleResponse> getMySchedule(
            @PathVariable String studentId,
            @RequestParam String semesterId) {
        MyScheduleResponse schedule = studentService.getMySchedule(studentId, semesterId);
        return ApiResponse.buildSuccessResponse(schedule, SuccessCode.GET_SCHEDULE_SUCCESSFUL);
    }

    @PostMapping("/{studentId}/enrollments")
    @PreAuthorize("hasRole('STUDENT') and authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    public ApiResponse<?> registerCourse(
            @PathVariable String studentId,
            @RequestParam String courseId) {
        if (asyncEnabled) {
            String requestId = registrationMessageProducer.sendRegistrationRequest(studentId, courseId);
            AsyncRegistrationResponse response = AsyncRegistrationResponse.builder()
                    .requestId(requestId)
                    .message("Registration request queued successfully")
                    .build();
            return ApiResponse.buildSuccessResponse(response, SuccessCode.REGISTER_COURSE_SUCCESSFUL);
        }
        Enrollment enrollment = registrationCommandService.registerCourse(studentId, courseId);
        return ApiResponse.buildSuccessResponse(enrollment, SuccessCode.REGISTER_COURSE_SUCCESSFUL);
    }

    @DeleteMapping("/{studentId}/enrollments/{enrollmentId}")
    @PreAuthorize("hasRole('STUDENT') and authentication.name == @studentRepository.findById(#studentId).get().getUser().getUsername()")
    public ApiResponse<Void> dropCourse(
            @PathVariable String studentId,
            @PathVariable String enrollmentId) {
        registrationCommandService.dropCourse(studentId, enrollmentId);
        return ApiResponse.buildSuccessResponse(null, SuccessCode.DROP_COURSE_SUCCESSFUL);
    }
}