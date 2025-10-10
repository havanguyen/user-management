package com.hanguyen.demo_spring_bai1.exception.registration;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyEnrolledException extends RuntimeException {
    public AlreadyEnrolledException(String subjectName) {
        super("You are already enrolled in a course for the subject: " + subjectName);
    }
}