package com.hanguyen.demo_spring_bai1.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NamedEntityGraph(
        name = "department-basic"  // Graph cơ bản, không cần fetch gì thêm
)
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) String id;
    String name;
    @Column(unique = true) String departmentCode;
}