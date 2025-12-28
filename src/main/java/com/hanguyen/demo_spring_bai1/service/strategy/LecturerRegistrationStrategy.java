package com.hanguyen.demo_spring_bai1.service.strategy;

import com.hanguyen.demo_spring_bai1.dto.request.RegisterRequest;
import com.hanguyen.demo_spring_bai1.entity.Department;
import com.hanguyen.demo_spring_bai1.entity.Lecturer;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.constant.ErrorCode;
import com.hanguyen.demo_spring_bai1.exception.AppException;
import com.hanguyen.demo_spring_bai1.constant.Roles;
import com.hanguyen.demo_spring_bai1.repository.DepartmentRepository;
import com.hanguyen.demo_spring_bai1.repository.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LecturerRegistrationStrategy implements RegistrationStrategy {

    private final DepartmentRepository departmentRepository;
    private final LecturerRepository lecturerRepository;

    @Override
    public void processRegistration(User user, RegisterRequest request) {
        if (request.getDepartmentId() == null) {
            throw new AppException(ErrorCode.DEPARTMENT_ID_REQUIRED);
        }
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Lecturer lecturerProfile = Lecturer.builder()
                .user(user)
                .lecturerCode(request.getLecturerCode())
                .degree(request.getDegree())
                .department(department)
                .build();
        lecturerRepository.save(lecturerProfile);
    }

    @Override
    public Roles supportedRole() {
        return Roles.LECTURER;
    }
}
