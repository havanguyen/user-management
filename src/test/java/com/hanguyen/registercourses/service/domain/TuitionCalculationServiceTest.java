package com.hanguyen.registercourses.service.domain;

import com.hanguyen.registercourses.constant.EnrollmentStatus;
import com.hanguyen.registercourses.entity.*;
import com.hanguyen.registercourses.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TuitionCalculationServiceTest {

    @Mock
    EnrollmentRepository enrollmentRepository;

    @InjectMocks
    TuitionCalculationService tuitionCalculationService;

    Student testStudent;
    Major testMajor;
    Subject subject3Credits;
    Subject subject4Credits;

    @BeforeEach
    void setUp() {
        testMajor = Major.builder()
                .id("major-1")
                .name("Computer Science")
                .pricePerCredit(BigDecimal.valueOf(500000))
                .build();

        testStudent = Student.builder()
                .id("student-1")
                .studentCode("STU001")
                .major(testMajor)
                .build();

        subject3Credits = Subject.builder().id("sub-1").name("Math").credits(3).build();
        subject4Credits = Subject.builder().id("sub-2").name("Physics").credits(4).build();
    }

    @Test
    @DisplayName("Should calculate tuition correctly")
    void calculateTuition_normalCase_calculatesCorrectly() {
        Course course1 = Course.builder().subject(subject3Credits).build();
        Course course2 = Course.builder().subject(subject4Credits).build();

        Enrollment e1 = Enrollment.builder()
                .status(EnrollmentStatus.REGISTERED)
                .course(course1)
                .build();
        Enrollment e2 = Enrollment.builder()
                .status(EnrollmentStatus.REGISTERED)
                .course(course2)
                .build();

        when(enrollmentRepository.findByStudentIdAndSemesterId("student-1", "sem-1"))
                .thenReturn(Arrays.asList(e1, e2));

        BigDecimal tuition = tuitionCalculationService.calculateTuition(testStudent, "sem-1");

        // 7 credits * 500,000 = 3,500,000
        assertEquals(BigDecimal.valueOf(3500000), tuition);
    }

    @Test
    @DisplayName("Should return zero when major has no price")
    void calculateTuition_noPricePerCredit_returnsZero() {
        testMajor.setPricePerCredit(null);

        BigDecimal tuition = tuitionCalculationService.calculateTuition(testStudent, "sem-1");

        assertEquals(BigDecimal.ZERO, tuition);
        verifyNoInteractions(enrollmentRepository);
    }

    @Test
    @DisplayName("Should return zero when student has no major")
    void calculateTuition_noMajor_returnsZero() {
        testStudent.setMajor(null);

        BigDecimal tuition = tuitionCalculationService.calculateTuition(testStudent, "sem-1");

        assertEquals(BigDecimal.ZERO, tuition);
    }

    @Test
    @DisplayName("Should only count REGISTERED enrollments")
    void calculateTotalCredits_onlyCountsRegistered() {
        Course course1 = Course.builder().subject(subject3Credits).build();
        Course course2 = Course.builder().subject(subject4Credits).build();

        Enrollment registered = Enrollment.builder()
                .status(EnrollmentStatus.REGISTERED)
                .course(course1)
                .build();
        Enrollment waitlist = Enrollment.builder()
                .status(EnrollmentStatus.WAITLIST)
                .course(course2)
                .build();

        when(enrollmentRepository.findByStudentIdAndSemesterId("student-1", "sem-1"))
                .thenReturn(Arrays.asList(registered, waitlist));

        int credits = tuitionCalculationService.calculateTotalCredits("student-1", "sem-1");

        assertEquals(3, credits); // Only the registered one counts
    }

    @Test
    @DisplayName("Should return true when exceeds credit limit")
    void exceedsCreditLimit_exceeds_returnsTrue() {
        Course course1 = Course.builder().subject(subject3Credits).build();
        Enrollment e1 = Enrollment.builder()
                .status(EnrollmentStatus.REGISTERED)
                .course(course1)
                .build();

        when(enrollmentRepository.findByStudentIdAndSemesterId("student-1", "sem-1"))
                .thenReturn(Collections.singletonList(e1));

        // Current: 3 credits, adding 22, limit is 24
        boolean exceeds = tuitionCalculationService.exceedsCreditLimit("student-1", "sem-1", 22, 24);

        assertTrue(exceeds); // 3 + 22 = 25 > 24
    }

    @Test
    @DisplayName("Should return false when within credit limit")
    void exceedsCreditLimit_withinLimit_returnsFalse() {
        Course course1 = Course.builder().subject(subject3Credits).build();
        Enrollment e1 = Enrollment.builder()
                .status(EnrollmentStatus.REGISTERED)
                .course(course1)
                .build();

        when(enrollmentRepository.findByStudentIdAndSemesterId("student-1", "sem-1"))
                .thenReturn(Collections.singletonList(e1));

        // Current: 3 credits, adding 20, limit is 24
        boolean exceeds = tuitionCalculationService.exceedsCreditLimit("student-1", "sem-1", 20, 24);

        assertFalse(exceeds); // 3 + 20 = 23 <= 24
    }
}
