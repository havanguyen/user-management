package com.hanguyen.demo_spring_bai1.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private boolean success;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
    private T data;
    private List<ValidationError> errors;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError() {}

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public List<ValidationError> getErrors() { return errors; }
    public void setErrors(List<ValidationError> errors) { this.errors = errors; }
}
