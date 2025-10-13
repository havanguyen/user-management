package com.hanguyen.demo_spring_bai1.entity;

import com.hanguyen.demo_spring_bai1.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NamedEntityGraph(
        name = "course-with-details",
        attributeNodes = {
                @NamedAttributeNode(value = "subject", subgraph = "subject-with-prerequisites"),
                @NamedAttributeNode("semester"),
                @NamedAttributeNode(value = "lecturer", subgraph = "lecturer-with-user-and-department")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "subject-with-prerequisites",
                        attributeNodes = {
                                @NamedAttributeNode("department"),
                                @NamedAttributeNode(value = "prerequisites", subgraph = "prerequisites-with-department")
                        }
                ),
                @NamedSubgraph(
                        name = "prerequisites-with-department",
                        attributeNodes = {
                                @NamedAttributeNode("department")
                        }
                ),
                @NamedSubgraph(
                        name = "lecturer-with-user-and-department",
                        attributeNodes = {
                                @NamedAttributeNode("user"),
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    Lecturer lecturer;
}