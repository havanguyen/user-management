package com.hanguyen.registercourses.service.strategy;
import com.hanguyen.registercourses.dto.request.RegisterRequest;
import com.hanguyen.registercourses.entity.Major;
import com.hanguyen.registercourses.entity.Student;
import com.hanguyen.registercourses.entity.User;
import com.hanguyen.registercourses.constant.ErrorCode;
import com.hanguyen.registercourses.exception.AppException;
import com.hanguyen.registercourses.constant.Roles;
import com.hanguyen.registercourses.repository.MajorRepository;
import com.hanguyen.registercourses.repository.StudentRepository;
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
