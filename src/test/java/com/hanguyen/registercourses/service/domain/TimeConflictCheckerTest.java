package com.hanguyen.registercourses.service.domain;

import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.CourseSchedule;
import com.hanguyen.registercourses.entity.Semester;
import com.hanguyen.registercourses.repository.CourseScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeConflictCheckerTest {

    @Mock
    CourseScheduleRepository courseScheduleRepository;

    @InjectMocks
    TimeConflictChecker timeConflictChecker;

    Course testCourse;
    Semester testSemester;

    @BeforeEach
    void setUp() {
        testSemester = Semester.builder().id("sem-1").name("Fall 2026").build();
        testCourse = Course.builder()
                .id("course-1")
                .courseCode("CS101")
                .semester(testSemester)
                .schedules(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should return false when course has no schedules")
    void hasTimeConflict_noSchedules_returnsFalse() {
        boolean result = timeConflictChecker.hasTimeConflict("student-1", testCourse);
        assertFalse(result);
        verifyNoInteractions(courseScheduleRepository);
    }

    @Test
    @DisplayName("Should return false when no conflict exists")
    void hasTimeConflict_noConflict_returnsFalse() {
        CourseSchedule schedule = CourseSchedule.builder()
                .dayOfWeek(2) // Monday
                .startPeriod(1)
                .endPeriod(3)
                .build();
        testCourse.getSchedules().add(schedule);

        when(courseScheduleRepository.existsConflictingSchedule(
                "student-1", "sem-1", 2, 1, 3))
                .thenReturn(false);

        boolean result = timeConflictChecker.hasTimeConflict("student-1", testCourse);

        assertFalse(result);
        verify(courseScheduleRepository).existsConflictingSchedule("student-1", "sem-1", 2, 1, 3);
    }

    @Test
    @DisplayName("Should return true when conflict exists")
    void hasTimeConflict_conflictExists_returnsTrue() {
        CourseSchedule schedule = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(1)
                .endPeriod(3)
                .build();
        testCourse.getSchedules().add(schedule);

        when(courseScheduleRepository.existsConflictingSchedule(
                "student-1", "sem-1", 2, 1, 3))
                .thenReturn(true);

        boolean result = timeConflictChecker.hasTimeConflict("student-1", testCourse);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should check all schedules and return true on first conflict")
    void hasTimeConflict_multipleSchedules_checksAllUntilConflict() {
        CourseSchedule schedule1 = CourseSchedule.builder()
                .dayOfWeek(2).startPeriod(1).endPeriod(3).build();
        CourseSchedule schedule2 = CourseSchedule.builder()
                .dayOfWeek(4).startPeriod(5).endPeriod(7).build();

        testCourse.getSchedules().add(schedule1);
        testCourse.getSchedules().add(schedule2);

        when(courseScheduleRepository.existsConflictingSchedule("student-1", "sem-1", 2, 1, 3))
                .thenReturn(false);
        when(courseScheduleRepository.existsConflictingSchedule("student-1", "sem-1", 4, 5, 7))
                .thenReturn(true);

        boolean result = timeConflictChecker.hasTimeConflict("student-1", testCourse);

        assertTrue(result);
    }
}
