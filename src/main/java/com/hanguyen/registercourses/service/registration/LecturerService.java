package com.hanguyen.registercourses.service.registration;
import com.hanguyen.registercourses.dto.request.GradeUpdateRequest;
import com.hanguyen.registercourses.dto.response.StudentInCourseResponse;
import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.Enrollment;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.repository.CourseRepository;
import com.hanguyen.registercourses.repository.EnrollmentRepository;
import com.hanguyen.registercourses.repository.LecturerRepository;
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
        if (!lecturerRepository.existsById(lecturerId)) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return courseRepository.findByLecturerIdAndSemesterId(lecturerId, semesterId);
    }
    public List<StudentInCourseResponse> getStudentListInCourse(String lecturerId, String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!course.getLecturer().getId().equals(lecturerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_VIEW_COURSE_STUDENTS);
        }
        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId);
        return enrollments.stream().map(enrollment -> StudentInCourseResponse.builder()
                .enrollmentId(enrollment.getId())
                .studentCode(enrollment.getStudent().getStudentCode())
                .studentFullName(enrollment.getStudent().getUser().getFirstname() + " "
                        + enrollment.getStudent().getUser().getLastname())
                .grade(enrollment.getGrade())
                .build()).collect(Collectors.toList());
    }
    @Transactional
    public Enrollment updateGrade(String lecturerId, GradeUpdateRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        if (!enrollment.getCourse().getLecturer().getId().equals(lecturerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_UPDATE_GRADE);
        }
        enrollment.setGrade(request.getGrade());
        return enrollmentRepository.save(enrollment);
    }
}