package com.hanguyen.demo_spring_bai1.service.strategy;

import com.hanguyen.demo_spring_bai1.dto.request.RegisterRequest;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.enums.Roles;

public interface RegistrationStrategy {
    void processRegistration(User user, RegisterRequest request);
    Roles supportedRole();
}
