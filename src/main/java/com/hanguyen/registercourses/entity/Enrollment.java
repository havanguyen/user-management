package com.hanguyen.registercourses.entity;

import com.hanguyen.registercourses.constant.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment", indexes = {
                @Index(name = "idx_enrollment_student_course", columnList = "student_id, course_id"),
                @Index(name = "idx_enrollment_course", columnList = "course_id"),
                @Index(name = "idx_enrollment_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Enrollment {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        String id;
        LocalDateTime registrationTime;
        Double grade;
        @Enumerated(EnumType.STRING)
        EnrollmentStatus status;
        Integer queueOrder;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "student_id")
        Student student;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "course_id")
        Course course;
        @CreatedDate
        @Column(updatable = false)
        LocalDateTime createdAt;
        @LastModifiedDate
        LocalDateTime updatedAt;
        @CreatedBy
        @Column(updatable = false)
        String createdBy;
        @LastModifiedBy
        String updatedBy;
}