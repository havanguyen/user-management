package com.hanguyen.registercourses.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseScheduleTest {

    @Test
    @DisplayName("Should detect conflict when schedules overlap on same day")
    void conflictsWith_overlapping_returnsTrue() {
        CourseSchedule schedule1 = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(1)
                .endPeriod(3)
                .build();

        CourseSchedule schedule2 = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(2)
                .endPeriod(5)
                .build();

        assertTrue(schedule1.conflictsWith(schedule2));
        assertTrue(schedule2.conflictsWith(schedule1));
    }

    @Test
    @DisplayName("Should not detect conflict when different days")
    void conflictsWith_differentDays_returnsFalse() {
        CourseSchedule schedule1 = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(1)
                .endPeriod(3)
                .build();

        CourseSchedule schedule2 = CourseSchedule.builder()
                .dayOfWeek(3)
                .startPeriod(1)
                .endPeriod(3)
                .build();

        assertFalse(schedule1.conflictsWith(schedule2));
    }

    @Test
    @DisplayName("Should not detect conflict when schedules are adjacent")
    void conflictsWith_adjacent_returnsFalse() {
        CourseSchedule schedule1 = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(1)
                .endPeriod(3)
                .build();

        CourseSchedule schedule2 = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(4)
                .endPeriod(6)
                .build();

        assertFalse(schedule1.conflictsWith(schedule2));
    }

    @Test
    @DisplayName("Should detect conflict when one schedule contains another")
    void conflictsWith_contained_returnsTrue() {
        CourseSchedule outer = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(1)
                .endPeriod(6)
                .build();

        CourseSchedule inner = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(2)
                .endPeriod(4)
                .build();

        assertTrue(outer.conflictsWith(inner));
        assertTrue(inner.conflictsWith(outer));
    }

    @Test
    @DisplayName("Should detect conflict when schedules share boundary")
    void conflictsWith_sharedBoundary_returnsTrue() {
        CourseSchedule schedule1 = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(1)
                .endPeriod(3)
                .build();

        CourseSchedule schedule2 = CourseSchedule.builder()
                .dayOfWeek(2)
                .startPeriod(3)
                .endPeriod(5)
                .build();

        assertTrue(schedule1.conflictsWith(schedule2));
    }
}
