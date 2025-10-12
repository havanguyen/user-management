package com.hanguyen.demo_spring_bai1.service;


import com.hanguyen.demo_spring_bai1.entity.*;
import com.hanguyen.demo_spring_bai1.enums.EnrollmentStatus;
import com.hanguyen.demo_spring_bai1.enums.ErrorCode;
import com.hanguyen.demo_spring_bai1.exception.BusinessException;
import com.hanguyen.demo_spring_bai1.exception.ResourceNotFoundException;
import com.hanguyen.demo_spring_bai1.exception.registration.AlreadyEnrolledException;
import com.hanguyen.demo_spring_bai1.exception.registration.CourseFullException;
import com.hanguyen.demo_spring_bai1.exception.registration.PrerequisiteNotMetException;
import com.hanguyen.demo_spring_bai1.repository.CourseRepository;
import com.hanguyen.demo_spring_bai1.repository.EnrollmentRepository;
import com.hanguyen.demo_spring_bai1.repository.RegistrationPeriodRepository;
import com.hanguyen.demo_spring_bai1.repository.StudentRepository;
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
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class RegistrationCommandService {

      CourseRepository courseRepository;
      RegistrationPeriodRepository registrationPeriodRepository;
      StudentRepository studentRepository;
      EnrollmentRepository enrollmentRepository;


    @Transactional
    public Enrollment registerCourse(String studentId, String courseId) {
        RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new BusinessException(ErrorCode.REGISTRATION_NOT_OPEN));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getSemester().getId().equals(activePeriod.getSemester().getId())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_IN_ACTIVE_PERIOD);
        }

        if (course.getCurrentStudents() >= course.getMaxStudents()) {
            throw new CourseFullException(course.getCourseCode());
        }

        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                studentId, course.getSemester().getId(), course.getSubject().getId()
        );
        if (alreadyEnrolled) {
            throw new AlreadyEnrolledException(course.getSubject().getName());
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
                .orElseThrow(() -> new BusinessException(ErrorCode.REGISTRATION_NOT_OPEN));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_DROP_COURSE);
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
                throw new PrerequisiteNotMetException("Prerequisite not met: You must pass " + prereq.getName());
            }
        }
    }
}
