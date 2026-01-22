package com.hanguyen.registercourses.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
    @Column(nullable = false)
    Integer dayOfWeek;
    @Column(nullable = false)
    Integer startPeriod;
    @Column(nullable = false)
    Integer endPeriod;
    String room;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;
    public boolean conflictsWith(CourseSchedule other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        return this.startPeriod <= other.endPeriod && other.startPeriod <= this.endPeriod;
    }
}
