package com.hanguyen.demo_spring_bai1.entity;

import com.hanguyen.demo_spring_bai1.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "enrollment-with-details",
        attributeNodes = {
                @NamedAttributeNode(value = "student", subgraph = "student-with-user-and-major"),
                @NamedAttributeNode(value = "course", subgraph = "course-with-lecturer-and-subject")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "student-with-user-and-major",
                        attributeNodes = {
                                @NamedAttributeNode("user"),
                                @NamedAttributeNode(value = "major", subgraph = "major-with-department")
                        }
                ),
                @NamedSubgraph(
                        name = "major-with-department",
                        attributeNodes = {
                                @NamedAttributeNode("department")
                        }
                ),
                @NamedSubgraph(
                        name = "course-with-lecturer-and-subject",
                        attributeNodes = {
                                @NamedAttributeNode(value = "lecturer", subgraph = "lecturer-with-user-and-department"),
                                @NamedAttributeNode(value = "subject", subgraph = "subject-with-department")
                        }
                ),
                @NamedSubgraph(
                        name = "lecturer-with-user-and-department",
                        attributeNodes = {
                                @NamedAttributeNode("user"),
                                @NamedAttributeNode("department")
                        }
                ),
                @NamedSubgraph(
                        name = "subject-with-department",
                        attributeNodes = {
                                @NamedAttributeNode("department")
                        }
                )
        }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    LocalDateTime registrationTime;

    Double grade;

    @Enumerated(EnumType.STRING)
    EnrollmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    Course course;
}