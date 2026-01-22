package com.hanguyen.registercourses.service.registration;

import com.hanguyen.registercourses.entity.*;
import com.hanguyen.registercourses.constant.EnrollmentStatus;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.repository.CourseRepository;
import com.hanguyen.registercourses.repository.EnrollmentRepository;
import com.hanguyen.registercourses.repository.RegistrationPeriodRepository;
import com.hanguyen.registercourses.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationCommandService {

    CourseRepository courseRepository;
    RegistrationPeriodRepository registrationPeriodRepository;
    StudentRepository studentRepository;
    EnrollmentRepository enrollmentRepository;

    @Transactional
    public Enrollment registerCourse(String studentId, String courseId) {
        RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_OPEN));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!course.getSemester().getId().equals(activePeriod.getSemester().getId())) {
            throw new AppException(ErrorCode.COURSE_NOT_IN_ACTIVE_PERIOD);
        }

        if (course.getCurrentStudents() >= course.getMaxStudents()) {
            throw new AppException(ErrorCode.COURSE_FULL);
        }

        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                studentId, course.getSemester().getId(), course.getSubject().getId());
        if (alreadyEnrolled) {
            throw new AppException(ErrorCode.ALREADY_ENROLLED);
        }

        checkPrerequisites(student, course.getSubject());

        course.setCurrentStudents(course.getCurrentStudents() + 1);
        courseRepository.save(course);

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .registrationTime(LocalDateTime.now())
                .status(EnrollmentStatus.REGISTERED)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void dropCourse(String studentId, String enrollmentId) {
        registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_OPEN));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_DROP_COURSE);
        }

        Course course = enrollment.getCourse();
        course.setCurrentStudents(course.getCurrentStudents() - 1);
        courseRepository.save(course);

        enrollmentRepository.delete(enrollment);
    }

    private void checkPrerequisites(Student student, Subject subject) {
        Set<Subject> prerequisites = subject.getPrerequisites();
        if (prerequisites.isEmpty()) {
            return;
        }

        Set<String> passedSubjectIds = enrollmentRepository.findAllByStudentId(student.getId()).stream()
                .filter(e -> e.getGrade() != null && e.getGrade() >= 4.0)
                .map(e -> e.getCourse().getSubject().getId())
                .collect(Collectors.toSet());

        for (Subject prereq : prerequisites) {
            if (!passedSubjectIds.contains(prereq.getId())) {
                throw new AppException(ErrorCode.PREREQUISITE_NOT_MET);
            }
        }
    }
}
