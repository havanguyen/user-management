package com.hanguyen.registercourses.exception;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.common.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public ApiResponse<?> handlingAppException(AppException exception, HttpServletResponse response) {
        return buildResponse(exception.getErrorCode(), null, response);
    }
    @ExceptionHandler(value = AccessDeniedException.class)
    public ApiResponse<?> handlingAccessDeniedException(HttpServletResponse response) {
        return buildResponse(ErrorCode.UNAUTHORIZED, null, response);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handlingHttpMessageNotReadableException(HttpMessageNotReadableException exception, HttpServletResponse response) {
        exception.getMostSpecificCause();
        String message = "Invalid request body: " + exception.getMostSpecificCause().getMessage();
        return buildResponse(ErrorCode.INVALID_KEY, message, response);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse<?> handlingNoResourceFoundExceptionException(NoResourceFoundException exception, HttpServletResponse response) {
        return buildResponse(ErrorCode.INVALID_KEY, null, response);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<?> handlingValidation(MethodArgumentNotValidException exception, HttpServletResponse response) {
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        String rawMessage = null;
        try {
            var firstError = exception.getBindingResult().getAllErrors().stream().findFirst().orElse(null);
            if (firstError != null) {
                rawMessage = firstError.getDefaultMessage();
                if (rawMessage != null) {
                    try {
                        errorCode = ErrorCode.valueOf(rawMessage);
                    } catch (IllegalArgumentException ignored) {
                    }
                    ConstraintViolation<?> constraintViolation = firstError.unwrap(ConstraintViolation.class);
                    if (constraintViolation.getConstraintDescriptor() != null) {
                        attributes = constraintViolation.getConstraintDescriptor().getAttributes();
                    }
                }
            }
        } catch (Exception ignored) {
        }
        String finalMessage = Objects.nonNull(attributes)
                ? mapAttribute(errorCode.getMessage(), attributes)
                : errorCode.getMessage();
        if (errorCode == ErrorCode.INVALID_KEY && rawMessage != null && !rawMessage.isEmpty()) {
            finalMessage = rawMessage;
        }
        return buildResponse(errorCode, finalMessage, response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, HttpServletResponse response) {
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
                .success(false)
                .statusCode(errorCode.getStatusCode().value())
                .message(message)
                .timestamp(Instant.now().toString())
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