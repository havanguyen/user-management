package com.hanguyen.registercourses.service.strategy;

import com.hanguyen.registercourses.dto.request.RegisterRequest;
import com.hanguyen.registercourses.entity.User;
import com.hanguyen.registercourses.constant.Roles;

public interface RegistrationStrategy {
    void processRegistration(User user, RegisterRequest request);
    Roles supportedRole();
}
