package com.hanguyen.demo_spring_bai1.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hanguyen.demo_spring_bai1.constant.SuccessCode;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    int code;
    String message;
    T result;

    public static <T> ApiResponse<T> ok(String message, T result) {
        return ApiResponse.<T>builder()
                .code(1000) // Default success code, assuming 1000 or derive from SuccessCode
                .message(message)
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> ok(T result) {
        return ApiResponse.<T>builder()
                .code(1000)
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> created(String message, T result) {
        return ApiResponse.<T>builder()
                .code(1000) // Or 201 equivalent
                .message(message)
                .result(result)
                .build();
    }

    static public <T> ApiResponse<T> buildSuccessResponse(T result, SuccessCode successCode) {
        return ApiResponse.<T>builder()
                .code(successCode.getStatusCode().value())
                .message(successCode.getMessage())
                .result(result)
                .build();
    }
}