package com.hanguyen.demo_spring_bai1.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "registrationPeriod-with-details",
        attributeNodes = {
                @NamedAttributeNode("semester")
        }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    LocalDateTime startTime;
    LocalDateTime endTime;
    boolean isActive;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", unique = true)
    Semester semester;
}