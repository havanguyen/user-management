package com.hanguyen.demo_spring_bai1.controller;

import com.hanguyen.demo_spring_bai1.dto.request.ApiResponse;
import com.hanguyen.demo_spring_bai1.dto.request.accademic.GradeUpdateRequest;
import com.hanguyen.demo_spring_bai1.dto.response.StudentInCourseResponse;
import com.hanguyen.demo_spring_bai1.entity.Course;
import com.hanguyen.demo_spring_bai1.entity.Enrollment;
import com.hanguyen.demo_spring_bai1.service.LecturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturer")
@RequiredArgsConstructor
public class LecturerController { //

    private final LecturerService lecturerService;

    @GetMapping("/{lecturerId}/courses")
    @PreAuthorize("hasRole('LECTURER') and authentication.name == @lecturerRepository.findById(#lecturerId).get().getUser().getUsername()")
    public ApiResponse<List<Course>> getAssignedCourses(
            @PathVariable String lecturerId,
            @RequestParam String semesterId
    ) {
        List<Course> courses = lecturerService.getAssignedCourses(lecturerId, semesterId);
        return ApiResponse.ok("Assigned courses fetched successfully", courses);
    }

    @GetMapping("/{lecturerId}/courses/{courseId}/students")
    @PreAuthorize("hasRole('LECTURER') and authentication.name == @lecturerRepository.findById(#lecturerId).get().getUser().getUsername()")
    public ApiResponse<List<StudentInCourseResponse>> getStudentList(
            @PathVariable String lecturerId,
            @PathVariable String courseId
    ) {
        List<StudentInCourseResponse> students = lecturerService.getStudentListInCourse(lecturerId, courseId);
        return ApiResponse.ok("Student list fetched successfully", students);
    }

    @PutMapping("/{lecturerId}/grades")
    @PreAuthorize("hasRole('LECTURER') and authentication.name == @lecturerRepository.findById(#lecturerId).get().getUser().getUsername()")
    public ApiResponse<Enrollment> updateGrade(
            @PathVariable String lecturerId,
            @Valid @RequestBody GradeUpdateRequest request
    ) {
        Enrollment updatedEnrollment = lecturerService.updateGrade(lecturerId, request);
        return ApiResponse.ok("Grade updated successfully", updatedEnrollment);
    }
}