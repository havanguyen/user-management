package com.hanguyen.demo_spring_bai1.exception;


import com.hanguyen.demo_spring_bai1.dto.request.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ApiResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiResponse.ValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setSuccess(false);
        response.setMessage("Validation failed");
        response.setErrors(validationErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setSuccess(false);
        response.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setSuccess(false);
        response.setMessage("Unexpected error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
