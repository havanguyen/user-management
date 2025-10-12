package com.hanguyen.demo_spring_bai1.dto.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * ✅ ApiResponse chuẩn REST: dùng cho tất cả các API trả về JSON thống nhất.
 * - Tự động sinh timestamp
 * - Có sẵn factory method cho các trạng thái HTTP phổ biến
 * - Giữ HttpStatus nội bộ để tiện xử lý
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"status", "message", "data", "timestamp"})
public class ApiResponse<T> {

    @JsonProperty("status")
    private int code;

    private String message;

    private T data;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Instant timestamp = Instant.now();

    @JsonIgnore
    private HttpStatus httpStatus;

    public static <T> ApiResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return ApiResponse.<T>builder()
                .httpStatus(httpStatus)
                .code(httpStatus.value())
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
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

    /* ===================== Error Factory Methods ===================== */

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return of(status, message, null);
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

    /* ===================== Convenience ===================== */

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return httpStatus != null ? httpStatus : HttpStatus.OK;
    }
}
