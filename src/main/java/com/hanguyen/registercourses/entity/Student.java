package com.hanguyen.registercourses.entity;

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