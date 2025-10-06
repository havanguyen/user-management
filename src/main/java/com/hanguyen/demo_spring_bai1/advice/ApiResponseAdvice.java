package com.hanguyen.demo_spring_bai1.advice;

import com.hanguyen.demo_spring_bai1.dto.request.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        if (body instanceof ApiResponse<?> apiResponse) {
            response.setStatusCode(apiResponse.getHttpStatus());
            return apiResponse;
        }

        if (body == null && returnType.getParameterType() == void.class) {
            ApiResponse<Void> apiResponse = ApiResponse.noContent();
            response.setStatusCode(apiResponse.getHttpStatus());
            return apiResponse;
        }

        ApiResponse<Object> apiResponse = ApiResponse.ok(body);
        response.setStatusCode(apiResponse.getHttpStatus());
        return apiResponse;
    }
}