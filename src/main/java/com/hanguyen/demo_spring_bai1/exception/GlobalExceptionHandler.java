package com.hanguyen.demo_spring_bai1.exception;

import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import com.hanguyen.demo_spring_bai1.dto.response.ApiResponse;
import com.hanguyen.demo_spring_bai1.constant.ErrorCode;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public ApiResponse<?> handlingAppException(AppException exception, HttpServletResponse response) {
        return buildResponse(exception.getErrorCode(), null, response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> handlingValidation(MethodArgumentNotValidException exception, HttpServletResponse response) {
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            var firstError = exception.getBindingResult().getAllErrors().stream().findFirst().orElse(null);
            if (firstError != null) {
                String enumKey = firstError.getDefaultMessage();
                if (enumKey != null) {
                    try {
                        errorCode = ErrorCode.valueOf(enumKey);
                    } catch (IllegalArgumentException ignored) {
                        log.warn("Invalid enum key: {}", enumKey);
                    }
                    ConstraintViolation<?> constraintViolation = firstError.unwrap(ConstraintViolation.class);
                    if (constraintViolation.getConstraintDescriptor() != null) {
                        attributes = constraintViolation.getConstraintDescriptor().getAttributes();
                    }
                }
            }
        } catch (Exception ignored) {
            log.error("Error processing validation error", ignored);
        }
        String finalMessage = Objects.nonNull(attributes)
                ? mapAttribute(errorCode.getMessage(), attributes)
                : errorCode.getMessage();
        return buildResponse(errorCode, finalMessage, response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException exception,
            HttpServletResponse response) {
        return buildResponse(ErrorCode.METHOD_NOT_ALLOWED, exception.getMessage(), response);
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResponse<?> handlingRuntimeException(Exception exception, HttpServletResponse response) {
        log.error("Exception: ", exception);
        return buildResponse(ErrorCode.UNCATEGORIZED_EXCEPTION, null, response);
    }

    private ApiResponse<?> buildResponse(ErrorCode errorCode, String customMessage, HttpServletResponse response) {
        response.setStatus(errorCode.getStatusCode().value());
        String message = (customMessage != null && !customMessage.isEmpty())
                ? customMessage
                : errorCode.getMessage();
        return ApiResponse.builder()
                .code(errorCode.getStatusCode().value())
                .message(message)
                .build();
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String value = String.valueOf(entry.getValue());
            String placeholder = "{" + entry.getKey() + "}";
            if (message.contains(placeholder)) {
                message = message.replace(placeholder, value);
            }
        }
        return message;
    }
}