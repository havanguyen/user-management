package com.hanguyen.demo_spring_bai1.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"status", "message", "data", "timestamp", "path"})
public class ApiResponse<T> {

    @JsonProperty("status")
    private int statusCode;

    private String message;
    private T data;
    private Instant timestamp;

    @JsonIgnore
    private HttpStatus httpStatus;

    private ApiResponse() {
        this.timestamp = Instant.now();
    }
    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, "Success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return of(HttpStatus.OK, message, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return of(HttpStatus.CREATED, "Created successfully", data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return of(HttpStatus.CREATED, message, data);
    }

    public static <T> ApiResponse<T> noContent() {
        return of(HttpStatus.NO_CONTENT, "No content", null);
    }
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return of(status, message, null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, T data) {
        return of(status, message, data);
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(HttpStatus.UNAUTHORIZED, message);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return error(HttpStatus.FORBIDDEN, message);
    }

    public static <T> ApiResponse<T> internalError(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return error(HttpStatus.CONFLICT, message);
    }

    private static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.httpStatus = httpStatus;
        response.statusCode = httpStatus.value();
        response.message = message;
        response.data = data;
        return response;
    }


    public ApiResponse<T> withPath(String path) {
        return this;
    }

    public ApiResponse<T> withRequestId(String requestId) {
        return this;
    }

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}