package com.hanguyen.demo_spring_bai1.controller;

import com.hanguyen.demo_spring_bai1.dto.request.ApiResponse;
import com.hanguyen.demo_spring_bai1.dto.request.accademic.CourseRequest;
import com.hanguyen.demo_spring_bai1.dto.request.accademic.RegistrationPeriodRequest;
import com.hanguyen.demo_spring_bai1.dto.request.accademic.SemesterRequest;
import com.hanguyen.demo_spring_bai1.dto.request.accademic.SubjectRequest;
import com.hanguyen.demo_spring_bai1.entity.Course;
import com.hanguyen.demo_spring_bai1.entity.RegistrationPeriod;
import com.hanguyen.demo_spring_bai1.entity.Semester;
import com.hanguyen.demo_spring_bai1.entity.Subject;
import com.hanguyen.demo_spring_bai1.service.accademic.CourseService;
import com.hanguyen.demo_spring_bai1.service.accademic.RegistrationPeriodService;
import com.hanguyen.demo_spring_bai1.service.accademic.SemesterService;
import com.hanguyen.demo_spring_bai1.service.accademic.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
// Lưu ý: Chúng ta sẽ thêm phần bảo mật (@PreAuthorize("hasRole('ADMIN')")) ở Giai đoạn 9
public class AdminController {

    private final SemesterService semesterService;
    private final SubjectService subjectService;
    private final CourseService courseService;
    private final RegistrationPeriodService registrationPeriodService;

    // ========================
    // == Semester Management ==
    // ========================

    @PostMapping("/semesters")
    public ApiResponse<Semester> createSemester(@Valid @RequestBody SemesterRequest request) {
        return ApiResponse.created("Semester created successfully", semesterService.createSemester(request));
    }

    @GetMapping("/semesters")
    public ApiResponse<List<Semester>> getAllSemesters() {
        return ApiResponse.ok(semesterService.getAllSemesters());
    }

    @GetMapping("/semesters/{id}")
    public ApiResponse<Semester> getSemesterById(@PathVariable String id) {
        return ApiResponse.ok(semesterService.getSemesterById(id));
    }

    // ========================
    // == Subject Management ==
    // ========================

    @PostMapping("/subjects")
    public ApiResponse<Subject> createSubject(@Valid @RequestBody SubjectRequest request) {
        return ApiResponse.created("Subject created successfully", subjectService.createSubject(request));
    }

    @GetMapping("/subjects")
    public ApiResponse<List<Subject>> getAllSubjects() {
        return ApiResponse.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/subjects/{id}")
    public ApiResponse<Subject> getSubjectById(@PathVariable String id) {
        return ApiResponse.ok(subjectService.getSubjectById(id));
    }

    // =======================
    // == Course Management ==
    // =======================

    @PostMapping("/courses")
    public ApiResponse<Course> createCourse(@Valid @RequestBody CourseRequest request) {
        Course newCourse = courseService.createCourse(request);
        return ApiResponse.created("Course created successfully", newCourse);
    }

    @GetMapping("/courses")
    public ApiResponse<List<Course>> getAllCourses() {
        return ApiResponse.ok(courseService.getAllCourses());
    }

    @GetMapping("/courses/{id}")
    public ApiResponse<Course> getCourseById(@PathVariable String id) {
        return ApiResponse.ok(courseService.getCourseById(id));
    }

    @GetMapping("/semesters/{semesterId}/courses")
    public ApiResponse<List<Course>> getCoursesBySemester(@PathVariable String semesterId) {
        return ApiResponse.ok(courseService.getCoursesBySemester(semesterId));
    }

    @PostMapping("/registration-periods")
    public ApiResponse<RegistrationPeriod> createRegistrationPeriod(@Valid @RequestBody RegistrationPeriodRequest request) {
        RegistrationPeriod newPeriod = registrationPeriodService.createPeriod(request);
        return ApiResponse.created("Registration period created successfully", newPeriod);
    }

    @GetMapping("/registration-periods")
    public ApiResponse<List<RegistrationPeriod>> getAllRegistrationPeriods() {
        return ApiResponse.ok(registrationPeriodService.getAllPeriods());
    }

    @PutMapping("/registration-periods/{id}/activate")
    public ApiResponse<RegistrationPeriod> activatePeriod(@PathVariable String id) {
        RegistrationPeriod period = registrationPeriodService.setActivePeriod(id, true);
        return ApiResponse.ok("Registration period activated", period);
    }

    @PutMapping("/registration-periods/{id}/deactivate")
    public ApiResponse<RegistrationPeriod> deactivatePeriod(@PathVariable String id) {
        RegistrationPeriod period = registrationPeriodService.setActivePeriod(id, false);
        return ApiResponse.ok("Registration period deactivated", period);
    }
}