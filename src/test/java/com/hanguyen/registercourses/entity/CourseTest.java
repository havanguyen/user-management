package com.hanguyen.registercourses.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    Course course;

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
        course = Course.builder()
                .id("course-1")
                .courseCode("CS101")
                .maxStudents(30)
                .currentStudents(0)
                .schedules(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should return true when slots available")
    void hasAvailableSlot_slotsAvailable_returnsTrue() {
        course.setCurrentStudents(29);
        assertTrue(course.hasAvailableSlot());
    }

    @Test
    @DisplayName("Should return false when course is full")
    void hasAvailableSlot_courseFull_returnsFalse() {
        course.setCurrentStudents(30);
        assertFalse(course.hasAvailableSlot());
    }

    @Test
    @DisplayName("Should increment student count")
    void incrementStudentCount_success() {
        course.setCurrentStudents(5);
        course.incrementStudentCount();
        assertEquals(6, course.getCurrentStudents());
    }

    @Test
    @DisplayName("Should throw exception when incrementing full course")
    void incrementStudentCount_courseFull_throwsException() {
        course.setCurrentStudents(30);
        assertThrows(IllegalStateException.class, () -> course.incrementStudentCount());
    }

    @Test
    @DisplayName("Should decrement student count")
    void decrementStudentCount_success() {
        course.setCurrentStudents(5);
        course.decrementStudentCount();
        assertEquals(4, course.getCurrentStudents());
    }

    @Test
    @DisplayName("Should not decrement below zero")
    void decrementStudentCount_atZero_staysZero() {
        course.setCurrentStudents(0);
        course.decrementStudentCount();
        assertEquals(0, course.getCurrentStudents());
    }

    @Test
    @DisplayName("Should detect time conflict between courses")
    void hasTimeConflictWith_conflicting_returnsTrue() {
        Course otherCourse = Course.builder()
                .schedules(new ArrayList<>())
                .build();

        TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
        TimeSlot slot2 = createTimeSlot(2, "07:50", "08:35");
        TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");
        TimeSlot slot4 = createTimeSlot(4, "09:35", "10:20");

        CourseSchedule schedule1 = CourseSchedule.builder()
                .dayOfWeek(2).startTimeSlot(slot1).endTimeSlot(slot3).build();
        CourseSchedule schedule2 = CourseSchedule.builder()
                .dayOfWeek(2).startTimeSlot(slot2).endTimeSlot(slot4).build();

        course.getSchedules().add(schedule1);
        otherCourse.getSchedules().add(schedule2);

        assertTrue(course.hasTimeConflictWith(otherCourse));
    }

    @Test
    @DisplayName("Should not detect conflict when no overlap")
    void hasTimeConflictWith_noOverlap_returnsFalse() {
        Course otherCourse = Course.builder()
                .schedules(new ArrayList<>())
                .build();

        TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
        TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");

        CourseSchedule schedule1 = CourseSchedule.builder()
                .dayOfWeek(2).startTimeSlot(slot1).endTimeSlot(slot3).build();
        CourseSchedule schedule2 = CourseSchedule.builder()
                .dayOfWeek(3).startTimeSlot(slot1).endTimeSlot(slot3).build();

        course.getSchedules().add(schedule1);
        otherCourse.getSchedules().add(schedule2);

        assertFalse(course.hasTimeConflictWith(otherCourse));
    }

    @Test
    @DisplayName("Should add schedule to course")
    void addSchedule_success() {
        TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
        TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");

        CourseSchedule schedule = CourseSchedule.builder()
                .dayOfWeek(2).startTimeSlot(slot1).endTimeSlot(slot3).build();

        course.addSchedule(schedule);

        assertEquals(1, course.getSchedules().size());
        assertEquals(course, schedule.getCourse());
    }
}
