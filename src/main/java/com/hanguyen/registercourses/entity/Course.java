package com.hanguyen.registercourses.entity;

import com.hanguyen.registercourses.constant.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course", indexes = {
                @Index(name = "idx_course_semester", columnList = "semester_id"),
                @Index(name = "idx_course_subject", columnList = "subject_id"),
                @Index(name = "idx_course_lecturer", columnList = "lecturer_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        String id;
        @Column(unique = true)
        String courseCode;
        int maxStudents;
        int currentStudents;
        @Deprecated
        String scheduleInfo;
        @Enumerated(EnumType.STRING)
        CourseStatus status;
        @Version
        Long version;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "subject_id")
        Subject subject;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "semester_id")
        Semester semester;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "lecturer_id")
        Lecturer lecturer;
        @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        List<CourseSchedule> schedules = new ArrayList<>();

        public boolean hasAvailableSlot() {
                return currentStudents < maxStudents;
        }

        public void incrementStudentCount() {
                if (!hasAvailableSlot()) {
                        throw new IllegalStateException("Course is full");
                }
                this.currentStudents++;
        }

        public void decrementStudentCount() {
                if (this.currentStudents > 0) {
                        this.currentStudents--;
                }
        }

        public boolean hasTimeConflictWith(Course other) {
                for (CourseSchedule mySchedule : this.schedules) {
                        for (CourseSchedule otherSchedule : other.schedules) {
                                if (mySchedule.conflictsWith(otherSchedule)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        public void addSchedule(CourseSchedule schedule) {
                schedules.add(schedule);
                schedule.setCourse(this);
        }
}