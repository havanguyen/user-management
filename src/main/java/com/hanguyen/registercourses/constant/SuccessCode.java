package com.hanguyen.registercourses.constant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import lombok.Getter;

@Getter
public enum SuccessCode {
    LOGIN_SUCCESSFUL("Login successful", HttpStatus.OK),
    REFRESH_TOKEN_SUCCESSFUL("Token refreshed successfully", HttpStatus.OK),
    LOGOUT_SUCCESSFUL("Logged out successfully", HttpStatus.OK),
    REGISTER_SUCCESSFUL("User registered successfully", HttpStatus.CREATED),
    CREATE_USER_SUCCESSFUL("User created successfully", HttpStatus.CREATED),
    GET_USER_SUCCESSFUL("Get user successfully", HttpStatus.OK),
    GET_ALL_USERS_SUCCESSFUL("Get all users successfully", HttpStatus.OK),
    UPDATE_USER_SUCCESSFUL("User updated successfully", HttpStatus.OK),
    DELETE_USER_SUCCESSFUL("User deleted successfully", HttpStatus.OK),
    CREATE_SEMESTER_SUCCESSFUL("Semester created successfully", HttpStatus.CREATED),
    GET_SEMESTER_SUCCESSFUL("Semester fetched successfully", HttpStatus.OK),
    GET_ALL_SEMESTERS_SUCCESSFUL("All semesters fetched successfully", HttpStatus.OK),
    CREATE_SUBJECT_SUCCESSFUL("Subject created successfully", HttpStatus.CREATED),
    GET_SUBJECT_SUCCESSFUL("Subject fetched successfully", HttpStatus.OK),
    GET_ALL_SUBJECTS_SUCCESSFUL("All subjects fetched successfully", HttpStatus.OK),
    CREATE_COURSE_SUCCESSFUL("Course created successfully", HttpStatus.CREATED),
    GET_COURSE_SUCCESSFUL("Course fetched successfully", HttpStatus.OK),
    GET_ALL_COURSES_SUCCESSFUL("All courses fetched successfully", HttpStatus.OK),
    GET_OPEN_COURSES_SUCCESSFUL("Open courses fetched successfully", HttpStatus.OK),
    DROP_COURSE_SUCCESSFUL("Course dropped successfully", HttpStatus.OK),
    REGISTER_COURSE_SUCCESSFUL("Course registered successfully", HttpStatus.CREATED),
    CREATE_REGISTRATION_PERIOD_SUCCESSFUL("Registration period created successfully", HttpStatus.CREATED),
    GET_ALL_REGISTRATION_PERIODS_SUCCESSFUL("All registration periods fetched successfully", HttpStatus.OK),
    ACTIVATE_PERIOD_SUCCESSFUL("Registration period activated", HttpStatus.OK),
    DEACTIVATE_PERIOD_SUCCESSFUL("Registration period deactivated", HttpStatus.OK),
    GET_SCHEDULE_SUCCESSFUL("Schedule fetched successfully", HttpStatus.OK),
    GET_ASSIGNED_COURSES_SUCCESSFUL("Assigned courses fetched successfully", HttpStatus.OK),
    GET_STUDENT_LIST_SUCCESSFUL("Student list fetched successfully", HttpStatus.OK),
    UPDATE_GRADE_SUCCESSFUL("Grade updated successfully", HttpStatus.OK),
    CHANGE_PASSWORD_SUCCESSFUL("Change password successful", HttpStatus.OK),
    GET_ALL_MAJORS_SUCCESSFUL("Get all majors successful", HttpStatus.OK),
    GET_ALL_DEPARTMENTS_SUCCESSFUL("Get all departments successful", HttpStatus.OK),
    ;

    SuccessCode(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    private final String message;
    private final HttpStatusCode statusCode;
}