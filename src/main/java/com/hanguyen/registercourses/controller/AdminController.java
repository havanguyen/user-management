package com.hanguyen.registercourses.controller;
import com.hanguyen.registercourses.dto.request.CourseRequest;
import com.hanguyen.registercourses.common.ApiResponse;
import com.hanguyen.registercourses.common.PageResponse;
import com.hanguyen.registercourses.constant.SuccessCode;
import com.hanguyen.registercourses.dto.request.RegistrationPeriodRequest;
import com.hanguyen.registercourses.dto.request.SemesterRequest;
import com.hanguyen.registercourses.dto.request.SubjectRequest;
import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.RegistrationPeriod;
import com.hanguyen.registercourses.entity.Semester;
import com.hanguyen.registercourses.entity.Subject;
import com.hanguyen.registercourses.service.accademic.CourseService;
import com.hanguyen.registercourses.service.accademic.RegistrationPeriodService;
import com.hanguyen.registercourses.service.accademic.SemesterService;
import com.hanguyen.registercourses.service.accademic.SubjectService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    SemesterService semesterService;
    SubjectService subjectService;
    CourseService courseService;
    RegistrationPeriodService registrationPeriodService;
    @PostMapping("/semesters")
    public ApiResponse<Semester> createSemester(@Valid @RequestBody SemesterRequest request) {
        return ApiResponse.buildSuccessResponse(semesterService.createSemester(request),
                SuccessCode.CREATE_SEMESTER_SUCCESSFUL);
    }
    @GetMapping("/semesters")
    public ApiResponse<PageResponse<Semester>> getAllSemesters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        return ApiResponse.buildSuccessResponse(
                PageResponse.fromPage(semesterService.getAllSemesters(page, size, sortBy)),
                SuccessCode.GET_ALL_SEMESTERS_SUCCESSFUL);
    }
    @GetMapping("/semesters/{id}")
    public ApiResponse<Semester> getSemesterById(@PathVariable String id) {
        return ApiResponse.buildSuccessResponse(semesterService.getSemesterById(id),
                SuccessCode.GET_SEMESTER_SUCCESSFUL);
    }
    @PostMapping("/subjects")
    public ApiResponse<Subject> createSubject(@Valid @RequestBody SubjectRequest request) {
        return ApiResponse.buildSuccessResponse(subjectService.createSubject(request),
                SuccessCode.CREATE_SUBJECT_SUCCESSFUL);
    }
    @GetMapping("/subjects")
    public ApiResponse<PageResponse<Subject>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        return ApiResponse.buildSuccessResponse(
                PageResponse.fromPage(subjectService.getAllSubjects(page, size, sortBy)),
                SuccessCode.GET_ALL_SUBJECTS_SUCCESSFUL);
    }
    @GetMapping("/subjects/{id}")
    public ApiResponse<Subject> getSubjectById(@PathVariable String id) {
        return ApiResponse.buildSuccessResponse(subjectService.getSubjectById(id), SuccessCode.GET_SUBJECT_SUCCESSFUL);
    }
    @PostMapping("/courses")
    public ApiResponse<Course> createCourse(@Valid @RequestBody CourseRequest request) {
        Course newCourse = courseService.createCourse(request);
        return ApiResponse.buildSuccessResponse(newCourse, SuccessCode.CREATE_COURSE_SUCCESSFUL);
    }
    @GetMapping("/courses")
    public ApiResponse<PageResponse<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "courseCode") String sortBy) {
        return ApiResponse.buildSuccessResponse(
                PageResponse.fromPage(courseService.getAllCourses(page, size, sortBy)),
                SuccessCode.GET_ALL_COURSES_SUCCESSFUL);
    }
    @GetMapping("/courses/{id}")
    public ApiResponse<Course> getCourseById(@PathVariable String id) {
        return ApiResponse.buildSuccessResponse(courseService.getCourseById(id), SuccessCode.GET_COURSE_SUCCESSFUL);
    }
    @GetMapping("/semesters/{semesterId}/courses")
    public ApiResponse<PageResponse<Course>> getCoursesBySemester(
            @PathVariable String semesterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "courseCode") String sortBy) {
        return ApiResponse.buildSuccessResponse(
                PageResponse.fromPage(courseService.getCoursesBySemester(semesterId, page, size, sortBy)),
                SuccessCode.GET_ALL_COURSES_SUCCESSFUL);
    }
    @PostMapping("/registration-periods")
    public ApiResponse<RegistrationPeriod> createRegistrationPeriod(
            @Valid @RequestBody RegistrationPeriodRequest request) {
        RegistrationPeriod newPeriod = registrationPeriodService.createPeriod(request);
        return ApiResponse.buildSuccessResponse(newPeriod, SuccessCode.CREATE_REGISTRATION_PERIOD_SUCCESSFUL);
    }
    @GetMapping("/registration-periods")
    public ApiResponse<PageResponse<RegistrationPeriod>> getAllRegistrationPeriods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy) {
        return ApiResponse.buildSuccessResponse(
                PageResponse.fromPage(registrationPeriodService.getAllPeriods(page, size, sortBy)),
                SuccessCode.GET_ALL_REGISTRATION_PERIODS_SUCCESSFUL);
    }
    @PutMapping("/registration-periods/{id}/activate")
    public ApiResponse<RegistrationPeriod> activatePeriod(@PathVariable String id) {
        RegistrationPeriod period = registrationPeriodService.setActivePeriod(id, true);
        return ApiResponse.buildSuccessResponse(period, SuccessCode.ACTIVATE_PERIOD_SUCCESSFUL);
    }
    @PutMapping("/registration-periods/{id}/deactivate")
    public ApiResponse<RegistrationPeriod> deactivatePeriod(@PathVariable String id) {
        RegistrationPeriod period = registrationPeriodService.setActivePeriod(id, false);
        return ApiResponse.buildSuccessResponse(period, SuccessCode.DEACTIVATE_PERIOD_SUCCESSFUL);
    }
}