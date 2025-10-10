package com.hanguyen.demo_spring_bai1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    LocalDate startDate;
    LocalDate endDate;
}