package com.hanguyen.registercourses.service.domain;

import com.hanguyen.registercourses.entity.Course;
import com.hanguyen.registercourses.entity.CourseSchedule;
import com.hanguyen.registercourses.entity.Semester;
import com.hanguyen.registercourses.entity.TimeSlot;
import com.hanguyen.registercourses.repository.CourseScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;

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

        private TimeSlot createTimeSlot(int period, String start, String end) {
                return TimeSlot.builder()
                                .periodNumber(period)
                                .startTime(LocalTime.parse(start))
                                .endTime(LocalTime.parse(end))
                                .displayName("Tiết " + period)
                                .isActive(true)
                                .build();
        }

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
                TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
                TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");

                CourseSchedule schedule = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot1)
                                .endTimeSlot(slot3)
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
                TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
                TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");

                CourseSchedule schedule = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot1)
                                .endTimeSlot(slot3)
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
                TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
                TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");
                TimeSlot slot5 = createTimeSlot(5, "10:25", "11:10");
                TimeSlot slot7 = createTimeSlot(7, "12:30", "13:15");

                CourseSchedule schedule1 = CourseSchedule.builder()
                                .dayOfWeek(2).startTimeSlot(slot1).endTimeSlot(slot3).build();
                CourseSchedule schedule2 = CourseSchedule.builder()
                                .dayOfWeek(4).startTimeSlot(slot5).endTimeSlot(slot7).build();

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
