package com.hanguyen.registercourses.controller;
import com.hanguyen.registercourses.dto.request.GradeUpdateRequest;
import com.hanguyen.registercourses.dto.response.StudentInCourseResponse;
import com.hanguyen.registercourses.common.ApiResponse;
import com.hanguyen.registercourses.constant.SuccessCode;
import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.Enrollment;
import com.hanguyen.registercourses.service.registration.LecturerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/lecturer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LecturerController {
    LecturerService lecturerService;
    @GetMapping("/{lecturerId}/courses")
    @PreAuthorize("hasRole('LECTURER') and authentication.name == @lecturerRepository.findById(#lecturerId).get().getUser().getUsername()")
    public ApiResponse<List<Course>> getAssignedCourses(
            @PathVariable String lecturerId,
            @RequestParam String semesterId) {
        List<Course> courses = lecturerService.getAssignedCourses(lecturerId, semesterId);
        return ApiResponse.buildSuccessResponse(courses, SuccessCode.GET_ASSIGNED_COURSES_SUCCESSFUL);
    }
    @GetMapping("/{lecturerId}/courses/{courseId}/students")
    @PreAuthorize("hasRole('LECTURER') and authentication.name == @lecturerRepository.findById(#lecturerId).get().getUser().getUsername()")
    public ApiResponse<List<StudentInCourseResponse>> getStudentList(
            @PathVariable String lecturerId,
            @PathVariable String courseId) {
        List<StudentInCourseResponse> students = lecturerService.getStudentListInCourse(lecturerId, courseId);
        return ApiResponse.buildSuccessResponse(students, SuccessCode.GET_STUDENT_LIST_SUCCESSFUL);
    }
    @PutMapping("/{lecturerId}/grades")
    @PreAuthorize("hasRole('LECTURER') and authentication.name == @lecturerRepository.findById(#lecturerId).get().getUser().getUsername()")
    public ApiResponse<Enrollment> updateGrade(
            @PathVariable String lecturerId,
            @Valid @RequestBody GradeUpdateRequest request) {
        Enrollment updatedEnrollment = lecturerService.updateGrade(lecturerId, request);
        return ApiResponse.buildSuccessResponse(updatedEnrollment, SuccessCode.UPDATE_GRADE_SUCCESSFUL);
    }
}