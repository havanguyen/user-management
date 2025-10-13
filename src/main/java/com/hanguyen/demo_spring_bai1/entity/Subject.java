package com.hanguyen.demo_spring_bai1.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@NamedEntityGraph(
        name = "subject-with-details",
        attributeNodes = {
                @NamedAttributeNode("department"),
                @NamedAttributeNode("prerequisites")  // Fetch ManyToMany
        }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true)
    String subjectCode;

    String name;
    int credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    Department department;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "prerequisite",
            joinColumns = @JoinColumn(name = "subject_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    Set<Subject> prerequisites;
}