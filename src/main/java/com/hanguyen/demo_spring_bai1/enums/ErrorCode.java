package com.hanguyen.demo_spring_bai1.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    INVALID_KEY(1001, "Invalid message key"),
    USER_EXISTED(1002, "User existed"),
    USERNAME_INVALID(1003, "Username must be at least 3 characters"),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters"),
    USER_NOT_FOUND(1005, "User not found"),
    INVALID_CREDENTIALS(1006, "Invalid username or password"),
    REFRESH_TOKEN_NOT_FOUND(1007, "Refresh token not found"),
    MAJOR_ID_REQUIRED(1008, "Major ID is required for student registration."),
    DEPARTMENT_ID_REQUIRED(1009, "Department ID is required for lecturer registration."),
    INVALID_ROLE(1010, "Invalid role for registration."),
    LECTURER_NOT_FOUND(1011, "Lecturer not found"),
    COURSE_NOT_FOUND(1012, "Course not found"),
    ENROLLMENT_NOT_FOUND(1013, "Enrollment not found"),
    MAJOR_NOT_FOUND(1014, "Major not found"),
    DEPARTMENT_NOT_FOUND(1015, "Department not found"),
    SEMESTER_NOT_FOUND(1016, "Semester not found"),
    SUBJECT_NOT_FOUND(1017, "Subject not found"),
    STUDENT_NOT_FOUND(1018, "Student not found"),
    REGISTRATION_PERIOD_NOT_FOUND(1019, "Registration period not found"),
    UNAUTHORIZED_VIEW_COURSE_STUDENTS(1020, "You are not authorized to view this course's student list."),
    UNAUTHORIZED_UPDATE_GRADE(1021, "You are not authorized to update grades for this course."),
    REGISTRATION_NOT_OPEN(1022, "Registration is not open."),
    COURSE_NOT_IN_ACTIVE_PERIOD(1023, "This course is not in the active registration period."),
    UNAUTHORIZED_DROP_COURSE(1024, "You are not authorized to drop this course."),
    ALREADY_ENROLLED(1025, "You are already enrolled in this subject this semester."),
    COURSE_FULL(1026, "The course is full."),
    PREREQUISITE_NOT_MET(1027, "Prerequisite not met: You must pass all required subjects."),
    REFRESH_TOKEN_EXPIRED(1028, "Refresh token was expired. Please make a new signin request"),
    ANOTHER_PERIOD_ACTIVE(1029, "Another registration period is already active. Please deactivate it first.");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
