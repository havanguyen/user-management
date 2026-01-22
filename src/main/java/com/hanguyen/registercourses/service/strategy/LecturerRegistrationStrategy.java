package com.hanguyen.registercourses.service.strategy;

import com.hanguyen.registercourses.dto.request.RegisterRequest;
import com.hanguyen.registercourses.entity.Department;
import com.hanguyen.registercourses.entity.Lecturer;
import com.hanguyen.registercourses.entity.User;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.constant.Roles;
import com.hanguyen.registercourses.repository.DepartmentRepository;
import com.hanguyen.registercourses.repository.LecturerRepository;
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
