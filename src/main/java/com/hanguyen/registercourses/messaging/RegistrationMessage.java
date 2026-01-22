package com.hanguyen.registercourses.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message DTO for async course registration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationMessage implements Serializable {

    private String studentId;
    private String courseId;
    private String requestId;
    private LocalDateTime timestamp;
    private RegistrationType type;

    public enum RegistrationType {
        REGISTER,
        DROP
    }
}
