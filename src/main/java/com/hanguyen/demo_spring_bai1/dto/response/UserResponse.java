package com.hanguyen.demo_spring_bai1.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id ;
    String username ;
    String password ;
    String firstname;
    String lastname ;
    LocalDate dod;
}
