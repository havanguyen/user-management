package com.hanguyen.registercourses.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;

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
        @JoinTable(name = "prerequisite", joinColumns = @JoinColumn(name = "subject_id"), inverseJoinColumns = @JoinColumn(name = "prerequisite_id"))
        Set<Subject> prerequisites;
}