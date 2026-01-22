package com.hanguyen.registercourses.entity;

import com.hanguyen.registercourses.constant.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(name = "course-with-details", attributeNodes = {
                @NamedAttributeNode(value = "subject", subgraph = "subject-with-prerequisites"),
                @NamedAttributeNode("semester"),
                @NamedAttributeNode(value = "lecturer", subgraph = "lecturer-with-user-and-department"),
                @NamedAttributeNode("schedules")
}, subgraphs = {
                @NamedSubgraph(name = "subject-with-prerequisites", attributeNodes = {
                                @NamedAttributeNode("department"),
                                @NamedAttributeNode(value = "prerequisites", subgraph = "prerequisites-with-department")
                }),
                @NamedSubgraph(name = "prerequisites-with-department", attributeNodes = {
                                @NamedAttributeNode("department")
                }),
                @NamedSubgraph(name = "lecturer-with-user-and-department", attributeNodes = {
                                @NamedAttributeNode("user"),
                                @NamedAttributeNode("department")
                })
})
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

        /**
         * @deprecated Use schedules instead
         */
        @Deprecated
        String scheduleInfo;

        @Enumerated(EnumType.STRING)
        CourseStatus status;

        /**
         * Optimistic Locking version - Prevents Race Condition
         */
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

        // ==================== Domain Logic ====================

        /**
         * Kiểm tra lớp học còn chỗ không
         */
        public boolean hasAvailableSlot() {
                return currentStudents < maxStudents;
        }

        /**
         * Tăng số sinh viên (thread-safe với @Version)
         * 
         * @throws IllegalStateException nếu lớp đã đầy
         */
        public void incrementStudentCount() {
                if (!hasAvailableSlot()) {
                        throw new IllegalStateException("Course is full");
                }
                this.currentStudents++;
        }

        /**
         * Giảm số sinh viên
         */
        public void decrementStudentCount() {
                if (this.currentStudents > 0) {
                        this.currentStudents--;
                }
        }

        /**
         * Kiểm tra xem lịch học có trùng với Course khác không
         */
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

        /**
         * Thêm lịch học
         */
        public void addSchedule(CourseSchedule schedule) {
                schedules.add(schedule);
                schedule.setCourse(this);
        }
}