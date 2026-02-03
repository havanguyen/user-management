package com.hanguyen.registercourses.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalTime;

@Entity
@Table(name = "time_slot", indexes = {
        @Index(name = "idx_time_slot_period", columnList = "periodNumber")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    @Column(nullable = false, unique = true)
    Integer periodNumber;
    @Column(nullable = false)
    LocalTime startTime;
    @Column(nullable = false)
    LocalTime endTime;
    String displayName;
    @Builder.Default
    boolean isActive = true;
}
