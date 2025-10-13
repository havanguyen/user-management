package com.hanguyen.demo_spring_bai1.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NamedEntityGraph(
        name = "student-with-details",
        attributeNodes = {
                @NamedAttributeNode("user"),
                @NamedAttributeNode(value = "major", subgraph = "major-with-department")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "major-with-department",
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
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true)
    String studentCode;

    int enrollmentYear;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    Major major;
}