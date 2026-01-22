package com.hanguyen.registercourses.service.registration;

import com.hanguyen.registercourses.constant.EnrollmentStatus;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.entity.*;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.repository.*;
import com.hanguyen.registercourses.service.domain.TimeConflictChecker;
import com.hanguyen.registercourses.service.domain.TuitionCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationCommandServiceTest {

    @Mock
    CourseRepository courseRepository;
    @Mock
    RegistrationPeriodRepository registrationPeriodRepository;
    @Mock
    StudentRepository studentRepository;
    @Mock
    EnrollmentRepository enrollmentRepository;
    @Mock
    TimeConflictChecker timeConflictChecker;
    @Mock
    TuitionCalculationService tuitionCalculationService;

    @InjectMocks
    RegistrationCommandService registrationCommandService;

    Semester semester;
    RegistrationPeriod activePeriod;
    Student student;
    Subject subject;
    Course course;

    @BeforeEach
    void setUp() {
        semester = Semester.builder().id("sem-1").name("Fall 2026").build();
        activePeriod = RegistrationPeriod.builder()
                .id("period-1")
                .semester(semester)
                .isActive(true)
                .build();

        student = Student.builder()
                .id("student-1")
                .studentCode("STU001")
                .build();

        subject = Subject.builder()
                .id("sub-1")
                .name("Math")
                .credits(3)
                .prerequisites(new HashSet<>())
                .build();

        course = Course.builder()
                .id("course-1")
                .courseCode("CS101")
                .maxStudents(30)
                .currentStudents(10)
                .semester(semester)
                .subject(subject)
                .schedules(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should register course successfully")
    void registerCourse_success() {
        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
        when(enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                "student-1", "sem-1", "sub-1")).thenReturn(false);
        when(timeConflictChecker.hasTimeConflict("student-1", course)).thenReturn(false);
        when(tuitionCalculationService.exceedsCreditLimit("student-1", "sem-1", 3, 24)).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = registrationCommandService.registerCourse("student-1", "course-1");

        assertNotNull(result);
        assertEquals(EnrollmentStatus.REGISTERED, result.getStatus());
        assertEquals(11, course.getCurrentStudents());
        verify(courseRepository).save(course);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Should throw exception when registration not open")
    void registerCourse_registrationNotOpen_throwsException() {
        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> registrationCommandService.registerCourse("student-1", "course-1"));

        assertEquals(ErrorCode.REGISTRATION_NOT_OPEN, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when student not found")
    void registerCourse_studentNotFound_throwsException() {
        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(studentRepository.findById("student-1")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> registrationCommandService.registerCourse("student-1", "course-1"));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when already enrolled")
    void registerCourse_alreadyEnrolled_throwsException() {
        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(true);

        AppException exception = assertThrows(AppException.class,
                () -> registrationCommandService.registerCourse("student-1", "course-1"));

        assertEquals(ErrorCode.ALREADY_ENROLLED, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when time conflict exists")
    void registerCourse_timeConflict_throwsException() {
        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
        when(enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                "student-1", "sem-1", "sub-1")).thenReturn(false);
        when(timeConflictChecker.hasTimeConflict("student-1", course)).thenReturn(true);

        AppException exception = assertThrows(AppException.class,
                () -> registrationCommandService.registerCourse("student-1", "course-1"));

        assertEquals(ErrorCode.TIME_CONFLICT, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when credit limit exceeded")
    void registerCourse_creditLimitExceeded_throwsException() {
        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
        when(enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                "student-1", "sem-1", "sub-1")).thenReturn(false);
        when(timeConflictChecker.hasTimeConflict("student-1", course)).thenReturn(false);
        when(tuitionCalculationService.exceedsCreditLimit("student-1", "sem-1", 3, 24)).thenReturn(true);

        AppException exception = assertThrows(AppException.class,
                () -> registrationCommandService.registerCourse("student-1", "course-1"));

        assertEquals(ErrorCode.CREDIT_LIMIT_EXCEEDED, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should add to waitlist when course is full")
    void registerCourse_courseFull_addsToWaitlist() {
        course.setCurrentStudents(30); // Full

        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
        when(enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                "student-1", "sem-1", "sub-1")).thenReturn(false);
        when(timeConflictChecker.hasTimeConflict("student-1", course)).thenReturn(false);
        when(tuitionCalculationService.exceedsCreditLimit("student-1", "sem-1", 3, 24)).thenReturn(false);
        when(enrollmentRepository.countWaitlistByCourseId("course-1")).thenReturn(5);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = registrationCommandService.registerCourse("student-1", "course-1");

        assertNotNull(result);
        assertEquals(EnrollmentStatus.WAITLIST, result.getStatus());
        assertEquals(6, result.getQueueOrder()); // 5 + 1
    }

    @Test
    @DisplayName("Should retry on optimistic locking failure")
    void registerCourse_optimisticLockFailure_retries() {
        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
        when(courseRepository.findById("course-1")).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId("student-1", "course-1")).thenReturn(false);
        when(enrollmentRepository.existsByStudentIdAndCourseSemesterIdAndCourseSubjectId(
                "student-1", "sem-1", "sub-1")).thenReturn(false);
        when(timeConflictChecker.hasTimeConflict("student-1", course)).thenReturn(false);
        when(tuitionCalculationService.exceedsCreditLimit("student-1", "sem-1", 3, 24)).thenReturn(false);

        // First call throws OptimisticLockingFailureException, second succeeds
        when(courseRepository.save(any(Course.class)))
                .thenThrow(new OptimisticLockingFailureException("Conflict"))
                .thenReturn(course);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = registrationCommandService.registerCourse("student-1", "course-1");

        assertNotNull(result);
        verify(courseRepository, times(2)).save(any(Course.class)); // Retried once
    }

    @Test
    @DisplayName("Should drop course successfully")
    void dropCourse_success() {
        Enrollment enrollment = Enrollment.builder()
                .id("enroll-1")
                .student(student)
                .course(course)
                .status(EnrollmentStatus.REGISTERED)
                .build();

        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.findWaitlistByCourseIdOrderByQueueOrder("course-1"))
                .thenReturn(Collections.emptyList());

        registrationCommandService.dropCourse("student-1", "enroll-1");

        verify(enrollmentRepository).delete(enrollment);
        verify(courseRepository).save(course);
        assertEquals(9, course.getCurrentStudents());
    }

    @Test
    @DisplayName("Should throw exception when unauthorized to drop")
    void dropCourse_unauthorized_throwsException() {
        Student otherStudent = Student.builder().id("student-2").build();
        Enrollment enrollment = Enrollment.builder()
                .id("enroll-1")
                .student(otherStudent)
                .course(course)
                .status(EnrollmentStatus.REGISTERED)
                .build();

        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));

        AppException exception = assertThrows(AppException.class,
                () -> registrationCommandService.dropCourse("student-1", "enroll-1"));

        assertEquals(ErrorCode.UNAUTHORIZED_DROP_COURSE, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should promote from waitlist when dropping registered enrollment")
    void dropCourse_promotesFromWaitlist() {
        Enrollment enrollment = Enrollment.builder()
                .id("enroll-1")
                .student(student)
                .course(course)
                .status(EnrollmentStatus.REGISTERED)
                .build();

        Student waitlistStudent = Student.builder().id("student-waitlist").build();
        Enrollment waitlistEnrollment = Enrollment.builder()
                .id("enroll-waitlist")
                .student(waitlistStudent)
                .course(course)
                .status(EnrollmentStatus.WAITLIST)
                .queueOrder(1)
                .build();

        when(registrationPeriodRepository.findByIsActiveTrue()).thenReturn(Optional.of(activePeriod));
        when(enrollmentRepository.findById("enroll-1")).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.findWaitlistByCourseIdOrderByQueueOrder("course-1"))
                .thenReturn(Collections.singletonList(waitlistEnrollment));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        registrationCommandService.dropCourse("student-1", "enroll-1");

        assertEquals(EnrollmentStatus.REGISTERED, waitlistEnrollment.getStatus());
        assertNull(waitlistEnrollment.getQueueOrder());
        verify(enrollmentRepository).save(waitlistEnrollment);
    }
}
