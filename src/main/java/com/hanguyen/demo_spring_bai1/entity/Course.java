package com.hanguyen.demo_spring_bai1.entity;

import com.hanguyen.demo_spring_bai1.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
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
    String scheduleInfo;

    @Enumerated(EnumType.STRING)
            CourseStatus status;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    Subject subject;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    Semester semester;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    Lecturer lecturer;
}