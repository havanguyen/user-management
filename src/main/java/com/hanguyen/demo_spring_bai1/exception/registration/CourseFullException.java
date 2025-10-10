package com.hanguyen.demo_spring_bai1.exception.registration;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CourseFullException extends RuntimeException {
    public CourseFullException(String courseCode) {
        super("Course " + courseCode + " is full.");
    }
}