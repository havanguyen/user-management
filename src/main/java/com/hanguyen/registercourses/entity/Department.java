package com.hanguyen.registercourses.entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
@NamedEntityGraph(name = "department-basic")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    @Column(unique = true)
    String departmentCode;
}