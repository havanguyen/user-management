package com.hanguyen.registercourses.common;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hanguyen.registercourses.constant.SuccessCode;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.Instant;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    boolean success;
    String message;
    T data;
    String timestamp;
    int statusCode ;
    static public  <T> ApiResponse<T> buildSuccessResponse(T data, SuccessCode successCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(successCode.getStatusCode().value())
                .message(successCode.getMessage())
                .data(data)
                .timestamp(Instant.now().toString())
                .build();
    }
}