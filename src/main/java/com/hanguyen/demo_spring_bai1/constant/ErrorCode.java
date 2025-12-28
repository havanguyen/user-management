package com.hanguyen.demo_spring_bai1.constant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION("Unknown error.", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_KEY("Invalid error message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED("User already exists", HttpStatus.BAD_REQUEST),
    INVALID_ROLE("Invalid role", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED("Refresh token expired", HttpStatus.UNAUTHORIZED),
    PASSWORD_WEAK("Password must be at least 8 characters long and include uppercase, lowercase, and numbers.",
            HttpStatus.BAD_REQUEST),
    INVALID_DOB("Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("Invalid email format", HttpStatus.BAD_REQUEST),
    INVALID_FIRSTNAME("First name cannot be empty", HttpStatus.BAD_REQUEST),
    INVALID_LASTNAME("Last name cannot be empty", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_CORRECT("Old password is not correct", HttpStatus.BAD_REQUEST),

    URL_NOT_FOUND("URL not found", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("HTTP Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),

    USER_NOT_EXISTED("User not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXISTED("Category not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_EXISTED("Product not found", HttpStatus.NOT_FOUND),
    RESOURCE_NOT_FOUND("Resource not found", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED("Unauthenticated", HttpStatus.UNAUTHORIZED),
    USERNAME_OR_PASSWORD_INCORRECT("Incorrect username or password.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS("Invalid credentials", HttpStatus.UNAUTHORIZED),

    // Resource Errors
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_NOT_FOUND("Refresh token not found", HttpStatus.NOT_FOUND),

    // Registration Errors
    COURSE_FULL("Course is full", HttpStatus.BAD_REQUEST),
    ALREADY_ENROLLED("Already enrolled in this course", HttpStatus.BAD_REQUEST),
    PREREQUISITE_NOT_MET("Prerequisite not met", HttpStatus.BAD_REQUEST),
    REGISTRATION_NOT_OPEN("Registration is not open", HttpStatus.BAD_REQUEST),
    COURSE_NOT_IN_ACTIVE_PERIOD("Course is not in active period", HttpStatus.BAD_REQUEST),
    ANOTHER_PERIOD_ACTIVE("Another registration period is already active", HttpStatus.BAD_REQUEST),

    // Validation Errors
    DEPARTMENT_ID_REQUIRED("Department ID is required", HttpStatus.BAD_REQUEST),
    MAJOR_ID_REQUIRED("Major ID is required", HttpStatus.BAD_REQUEST),

    // Authorization Errors
    UNAUTHORIZED_DROP_COURSE("You are not authorized to drop this course", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_VIEW_COURSE_STUDENTS("You are not authorized to view students in this course", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_UPDATE_GRADE("You are not authorized to update grades for this course", HttpStatus.FORBIDDEN),
    ;

    ErrorCode(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    private final String message;
    private final HttpStatusCode statusCode;
}