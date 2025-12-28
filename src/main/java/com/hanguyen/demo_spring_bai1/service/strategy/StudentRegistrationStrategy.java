package com.hanguyen.demo_spring_bai1.service.strategy;

import com.hanguyen.demo_spring_bai1.dto.request.RegisterRequest;
import com.hanguyen.demo_spring_bai1.entity.Major;
import com.hanguyen.demo_spring_bai1.entity.Student;
import com.hanguyen.demo_spring_bai1.entity.User;
import com.hanguyen.demo_spring_bai1.constant.ErrorCode;
import com.hanguyen.demo_spring_bai1.exception.AppException;
import com.hanguyen.demo_spring_bai1.constant.Roles;
import com.hanguyen.demo_spring_bai1.repository.MajorRepository;
import com.hanguyen.demo_spring_bai1.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentRegistrationStrategy implements RegistrationStrategy {

    private final MajorRepository majorRepository;
    private final StudentRepository studentRepository;

    @Override
    public void processRegistration(User user, RegisterRequest request) {
        if (request.getMajorId() == null) {
            throw new AppException(ErrorCode.MAJOR_ID_REQUIRED);
        }
        Major major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Student studentProfile = Student.builder()
                .user(user)
                .studentCode(request.getStudentCode())
                .enrollmentYear(request.getEnrollmentYear())
                .major(major)
                .build();
        studentRepository.save(studentProfile);
    }

    @Override
    public Roles supportedRole() {
        return Roles.STUDENT;
    }
}
