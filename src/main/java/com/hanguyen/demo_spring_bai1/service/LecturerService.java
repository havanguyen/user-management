package com.hanguyen.demo_spring_bai1.service;

import com.hanguyen.demo_spring_bai1.dto.request.accademic.GradeUpdateRequest;
import com.hanguyen.demo_spring_bai1.dto.response.StudentInCourseResponse;
import com.hanguyen.demo_spring_bai1.entity.Course;
import com.hanguyen.demo_spring_bai1.entity.Enrollment;
import com.hanguyen.demo_spring_bai1.exception.BusinessException;
import com.hanguyen.demo_spring_bai1.exception.ResourceNotFoundException;
import com.hanguyen.demo_spring_bai1.repository.CourseRepository;
import com.hanguyen.demo_spring_bai1.repository.EnrollmentRepository;
import com.hanguyen.demo_spring_bai1.repository.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LecturerRepository lecturerRepository;

    public List<Course> getAssignedCourses(String lecturerId, String semesterId) {
        // Kiểm tra giảng viên tồn tại
        if (!lecturerRepository.existsById(lecturerId)) {
            throw new ResourceNotFoundException("Lecturer", "id", lecturerId);
        }
        return courseRepository.findByLecturerIdAndSemesterId(lecturerId, semesterId);
    }

    public List<StudentInCourseResponse> getStudentListInCourse(String lecturerId, String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Xác thực giảng viên này có quyền xem lớp này không
        if (!course.getLecturer().getId().equals(lecturerId)) {
            throw new BusinessException("You are not authorized to view this course's student list.");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId);

        return enrollments.stream().map(enrollment -> StudentInCourseResponse.builder()
                .enrollmentId(enrollment.getId())
                .studentCode(enrollment.getStudent().getStudentCode())
                .studentFullName(enrollment.getStudent().getUser().getFirstname() + " " + enrollment.getStudent().getUser().getLastname())
                .grade(enrollment.getGrade())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public Enrollment updateGrade(String lecturerId, GradeUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", request.getEnrollmentId()));

        // Xác thực giảng viên có quyền nhập điểm cho lớp này không
        if (!enrollment.getCourse().getLecturer().getId().equals(lecturerId)) {
            throw new BusinessException("You are not authorized to update grades for this course.");
        }

        enrollment.setGrade(request.getGrade());
        return enrollmentRepository.save(enrollment);
    }
}