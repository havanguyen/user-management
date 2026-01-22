package com.hanguyen.registercourses.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id ;
    String username ;
    String firstname;
    String lastname ;
    LocalDate dod;
    Set<String> roles;
}
