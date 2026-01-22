package com.hanguyen.registercourses.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorResponse {
    String id;
    String name;
    String majorCode;
    BigDecimal pricePerCredit;
    String departmentId;
    String departmentName;
}
