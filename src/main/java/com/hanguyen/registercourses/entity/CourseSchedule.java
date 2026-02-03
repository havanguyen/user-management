package com.hanguyen.registercourses.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalTime;

@Entity
@Table(name = "course_schedule", indexes = {
        @Index(name = "idx_schedule_time", columnList = "dayOfWeek, start_time_slot_id, end_time_slot_id"),
        @Index(name = "idx_schedule_course", columnList = "course_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(nullable = false)
    Integer dayOfWeek;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_time_slot_id", nullable = false)
    TimeSlot startTimeSlot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_time_slot_id", nullable = false)
    TimeSlot endTimeSlot;
    String room;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    public boolean conflictsWith(CourseSchedule other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        return this.startTimeSlot.getPeriodNumber() <= other.endTimeSlot.getPeriodNumber()
                && other.startTimeSlot.getPeriodNumber() <= this.endTimeSlot.getPeriodNumber();
    }

    public LocalTime getActualStartTime() {
        return startTimeSlot != null ? startTimeSlot.getStartTime() : null;
    }

    public LocalTime getActualEndTime() {
        return endTimeSlot != null ? endTimeSlot.getEndTime() : null;
    }

    public Integer getStartPeriod() {
        return startTimeSlot != null ? startTimeSlot.getPeriodNumber() : null;
    }

    public Integer getEndPeriod() {
        return endTimeSlot != null ? endTimeSlot.getPeriodNumber() : null;
    }
}
