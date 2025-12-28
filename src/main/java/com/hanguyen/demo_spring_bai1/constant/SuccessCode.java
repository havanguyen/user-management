package com.hanguyen.demo_spring_bai1.constant;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum SuccessCode {
    LOGIN_SUCCESSFUL("Login successful", HttpStatus.OK),
    REFRESH_TOKEN_SUCCESSFUL("Refresh Token successful", HttpStatus.ACCEPTED),
    LOGOUT_SUCCESSFUL("Logout successful", HttpStatus.OK),

    CREATE_USER_SUCCESSFUL("Create user successful", HttpStatus.CREATED),
    GET_USER_SUCCESSFUL("Get user successful", HttpStatus.OK),
    GET_ALL_USERS_SUCCESSFUL("Get all users successful", HttpStatus.OK),
    UPDATE_USER_SUCCESSFUL("Update user successful", HttpStatus.OK),
    DELETE_USER_SUCCESSFUL("Delete user successful", HttpStatus.OK),

    CREATE_CATEGORY_SUCCESSFUL("Create category successful", HttpStatus.CREATED),
    GET_CATEGORY_SUCCESSFUL("Get category successful", HttpStatus.OK),
    GET_ALL_CATEGORIES_SUCCESSFUL("Get all categories successful", HttpStatus.OK),
    UPDATE_CATEGORY_SUCCESSFUL("Update category successful", HttpStatus.OK),
    DELETE_CATEGORY_SUCCESSFUL("Delete category successful", HttpStatus.OK),

    CREATE_PRODUCT_SUCCESSFUL("Create product successful", HttpStatus.CREATED),
    GET_PRODUCT_SUCCESSFUL("Get product successful", HttpStatus.OK),
    GET_ALL_PRODUCTS_SUCCESSFUL("Get all products successful", HttpStatus.OK),
    UPDATE_PRODUCT_SUCCESSFUL("Update product successful", HttpStatus.OK),
    DELETE_PRODUCT_SUCCESSFUL("Delete product successful", HttpStatus.OK),

    CHANGE_PASSWORD_SUCCESSFUL("Change password successful", HttpStatus.OK),
    ;

    SuccessCode(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    private final String message;
    private final HttpStatusCode statusCode;
}