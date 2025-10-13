package com.hanguyen.demo_spring_bai1.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NamedEntityGraph(
        name = "major-with-details",
        attributeNodes = {
                @NamedAttributeNode("department")
        }
)
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Major {
    @Id @GeneratedValue(strategy = GenerationType.UUID) String id;
    String name;
    @Column(unique = true) String majorCode;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "department_id") Department department;
}