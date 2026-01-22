package com.hanguyen.registercourses.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@NamedEntityGraph(name = "major-with-details", attributeNodes = {
                @NamedAttributeNode("department")
})
@Entity
@Table(name = "major")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Major {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        String id;

        String name;

        @Column(unique = true)
        String majorCode;

        /**
         * Đơn giá 1 tín chỉ cho sinh viên thuộc ngành này
         */
        @Column(precision = 10, scale = 2)
        BigDecimal pricePerCredit;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "department_id")
        Department department;
}