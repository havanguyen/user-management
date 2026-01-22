package com.hanguyen.registercourses.constant;

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

    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_NOT_FOUND("Refresh token not found", HttpStatus.NOT_FOUND),

    COURSE_FULL("Course is full", HttpStatus.BAD_REQUEST),
    ALREADY_ENROLLED("Already enrolled in this course", HttpStatus.BAD_REQUEST),
    PREREQUISITE_NOT_MET("Prerequisite not met", HttpStatus.BAD_REQUEST),
    REGISTRATION_NOT_OPEN("Registration is not open", HttpStatus.BAD_REQUEST),
    COURSE_NOT_IN_ACTIVE_PERIOD("Course is not in active period", HttpStatus.BAD_REQUEST),
    ANOTHER_PERIOD_ACTIVE("Another registration period is already active", HttpStatus.BAD_REQUEST),

    DEPARTMENT_ID_REQUIRED("Department ID is required", HttpStatus.BAD_REQUEST),
    MAJOR_ID_REQUIRED("Major ID is required", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED_DROP_COURSE("You are not authorized to drop this course", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_VIEW_COURSE_STUDENTS("You are not authorized to view students in this course", HttpStatus.FORBIDDEN),
    UNAUTHORIZED_UPDATE_GRADE("You are not authorized to update grades for this course", HttpStatus.FORBIDDEN),

    USERNAME_BLANK("Username cannot be blank", HttpStatus.BAD_REQUEST),
    USERNAME_SIZE("Username must be between 3 and 20 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_BLANK("Password cannot be blank", HttpStatus.BAD_REQUEST),
    PASSWORD_SIZE("Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    FIRSTNAME_BLANK("First name cannot be blank", HttpStatus.BAD_REQUEST),
    FIRSTNAME_SIZE("First name must be less than 50 characters", HttpStatus.BAD_REQUEST),
    LASTNAME_BLANK("Last name cannot be blank", HttpStatus.BAD_REQUEST),
    LASTNAME_SIZE("Last name must be less than 50 characters", HttpStatus.BAD_REQUEST),
    DOB_PAST("Date of birth must be in the past", HttpStatus.BAD_REQUEST),
    ROLE_REQUIRED("Role must be specified", HttpStatus.BAD_REQUEST),

    COURSE_CODE_REQUIRED("Course code is required", HttpStatus.BAD_REQUEST),
    MAX_STUDENTS_MIN("Max students must be at least 1", HttpStatus.BAD_REQUEST),
    SCHEDULE_INFO_REQUIRED("Schedule info is required", HttpStatus.BAD_REQUEST),
    SUBJECT_ID_REQUIRED("Subject ID is required", HttpStatus.BAD_REQUEST),
    SEMESTER_ID_REQUIRED("Semester ID is required", HttpStatus.BAD_REQUEST),
    LECTURER_ID_REQUIRED("Lecturer ID is required", HttpStatus.BAD_REQUEST),

    ENROLLMENT_ID_REQUIRED("Enrollment ID is required", HttpStatus.BAD_REQUEST),
    GRADE_REQUIRED("Grade is required", HttpStatus.BAD_REQUEST),
    GRADE_RANGE("Grade must be between 0 and 10", HttpStatus.BAD_REQUEST),

    SEMESTER_NAME_BLANK("Semester name cannot be blank", HttpStatus.BAD_REQUEST),
    START_DATE_REQUIRED("Start date is required", HttpStatus.BAD_REQUEST),
    END_DATE_REQUIRED("End date is required", HttpStatus.BAD_REQUEST),
    END_DATE_FUTURE("End date must be in the future", HttpStatus.BAD_REQUEST),

    SUBJECT_CODE_REQUIRED("Subject code is required", HttpStatus.BAD_REQUEST),
    SUBJECT_NAME_REQUIRED("Subject name is required", HttpStatus.BAD_REQUEST),
    CREDITS_MIN("Credits must be at least 1", HttpStatus.BAD_REQUEST),
    DEPARTMENT_ID_REQUIRED_FIELD("Department ID is required", HttpStatus.BAD_REQUEST),

    START_TIME_REQUIRED("Start time is required", HttpStatus.BAD_REQUEST),
    END_TIME_REQUIRED("End time is required", HttpStatus.BAD_REQUEST),
    END_TIME_FUTURE("End time must be in the future", HttpStatus.BAD_REQUEST),

    // New error codes for enhanced registration
    TIME_CONFLICT("Schedule conflict with another registered course", HttpStatus.BAD_REQUEST),
    CREDIT_LIMIT_EXCEEDED("Credit limit exceeded for this semester", HttpStatus.BAD_REQUEST),
    COURSE_REGISTRATION_CONFLICT("Registration conflict, please try again", HttpStatus.CONFLICT),
    ;

    ErrorCode(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    private final String message;
    private final HttpStatusCode statusCode;
}