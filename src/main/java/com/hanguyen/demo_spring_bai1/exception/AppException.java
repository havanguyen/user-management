package com.hanguyen.demo_spring_bai1.exception;

import com.hanguyen.demo_spring_bai1.constant.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppException extends RuntimeException {
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    private ErrorCode errorCode;
}