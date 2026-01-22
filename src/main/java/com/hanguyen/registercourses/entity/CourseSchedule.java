package com.hanguyen.registercourses.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * CourseSchedule - Chi tiết lịch học của một lớp học phần
 * Một Course có thể có nhiều buổi học (1-N relationship)
 */
@Entity
@Table(name = "course_schedule", indexes = {
        @Index(name = "idx_schedule_time", columnList = "dayOfWeek, startPeriod, endPeriod"),
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

    /**
     * Ngày trong tuần: 2 = Thứ Hai, 3 = Thứ Ba, ..., 8 = Chủ nhật
     */
    @Column(nullable = false)
    Integer dayOfWeek;

    /**
     * Tiết bắt đầu (1-12)
     */
    @Column(nullable = false)
    Integer startPeriod;

    /**
     * Tiết kết thúc (1-12)
     */
    @Column(nullable = false)
    Integer endPeriod;

    /**
     * Phòng học
     */
    String room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    /**
     * Kiểm tra xem lịch này có trùng với lịch khác không
     */
    public boolean conflictsWith(CourseSchedule other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        // Kiểm tra overlap: start1 <= end2 AND start2 <= end1
        return this.startPeriod <= other.endPeriod && other.startPeriod <= this.endPeriod;
    }
}
