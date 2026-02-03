package com.hanguyen.registercourses.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class CourseScheduleTest {

        private TimeSlot createTimeSlot(int period, String start, String end) {
                return TimeSlot.builder()
                                .periodNumber(period)
                                .startTime(LocalTime.parse(start))
                                .endTime(LocalTime.parse(end))
                                .displayName("Tiết " + period)
                                .isActive(true)
                                .build();
        }

        @Test
        @DisplayName("Should detect conflict when schedules overlap on same day")
        void conflictsWith_overlapping_returnsTrue() {
                TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
                TimeSlot slot2 = createTimeSlot(2, "07:50", "08:35");
                TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");
                TimeSlot slot5 = createTimeSlot(5, "10:25", "11:10");

                CourseSchedule schedule1 = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot1)
                                .endTimeSlot(slot3)
                                .build();

                CourseSchedule schedule2 = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot2)
                                .endTimeSlot(slot5)
                                .build();

                assertTrue(schedule1.conflictsWith(schedule2));
                assertTrue(schedule2.conflictsWith(schedule1));
        }

        @Test
        @DisplayName("Should not detect conflict when different days")
        void conflictsWith_differentDays_returnsFalse() {
                TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
                TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");

                CourseSchedule schedule1 = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot1)
                                .endTimeSlot(slot3)
                                .build();

                CourseSchedule schedule2 = CourseSchedule.builder()
                                .dayOfWeek(3)
                                .startTimeSlot(slot1)
                                .endTimeSlot(slot3)
                                .build();

                assertFalse(schedule1.conflictsWith(schedule2));
        }

        @Test
        @DisplayName("Should not detect conflict when schedules are adjacent")
        void conflictsWith_adjacent_returnsFalse() {
                TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
                TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");
                TimeSlot slot4 = createTimeSlot(4, "09:35", "10:20");
                TimeSlot slot6 = createTimeSlot(6, "11:15", "12:00");

                CourseSchedule schedule1 = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot1)
                                .endTimeSlot(slot3)
                                .build();

                CourseSchedule schedule2 = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot4)
                                .endTimeSlot(slot6)
                                .build();

                assertFalse(schedule1.conflictsWith(schedule2));
        }

        @Test
        @DisplayName("Should detect conflict when one schedule contains another")
        void conflictsWith_contained_returnsTrue() {
                TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
                TimeSlot slot2 = createTimeSlot(2, "07:50", "08:35");
                TimeSlot slot4 = createTimeSlot(4, "09:35", "10:20");
                TimeSlot slot6 = createTimeSlot(6, "11:15", "12:00");

                CourseSchedule outer = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot1)
                                .endTimeSlot(slot6)
                                .build();

                CourseSchedule inner = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot2)
                                .endTimeSlot(slot4)
                                .build();

                assertTrue(outer.conflictsWith(inner));
                assertTrue(inner.conflictsWith(outer));
        }

        @Test
        @DisplayName("Should detect conflict when schedules share boundary")
        void conflictsWith_sharedBoundary_returnsTrue() {
                TimeSlot slot1 = createTimeSlot(1, "07:00", "07:45");
                TimeSlot slot3 = createTimeSlot(3, "08:40", "09:25");
                TimeSlot slot5 = createTimeSlot(5, "10:25", "11:10");

                CourseSchedule schedule1 = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot1)
                                .endTimeSlot(slot3)
                                .build();

                CourseSchedule schedule2 = CourseSchedule.builder()
                                .dayOfWeek(2)
                                .startTimeSlot(slot3)
                                .endTimeSlot(slot5)
                                .build();

                assertTrue(schedule1.conflictsWith(schedule2));
        }
}
