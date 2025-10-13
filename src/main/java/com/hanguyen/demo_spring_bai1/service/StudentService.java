package com.hanguyen.demo_spring_bai1.service;

import com.hanguyen.demo_spring_bai1.dto.response.CourseResponse;
import com.hanguyen.demo_spring_bai1.dto.response.MyScheduleResponse;
import com.hanguyen.demo_spring_bai1.entity.*;
import com.hanguyen.demo_spring_bai1.enums.ErrorCode;
import com.hanguyen.demo_spring_bai1.exception.BusinessException;
import com.hanguyen.demo_spring_bai1.exception.ResourceNotFoundException;
import com.hanguyen.demo_spring_bai1.mapper.CourseMapper; // Import mapper
import com.hanguyen.demo_spring_bai1.repository.CourseRepository;
import com.hanguyen.demo_spring_bai1.repository.EnrollmentRepository;
import com.hanguyen.demo_spring_bai1.repository.RegistrationPeriodRepository;
import com.hanguyen.demo_spring_bai1.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import transactional

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final CourseRepository courseRepository;
    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper; // Inject mapper

    @Transactional(readOnly = true) // Thêm Transactional để giữ session
    public List<CourseResponse> getOpenCoursesForRegistration() {
        RegistrationPeriod activePeriod = registrationPeriodRepository.findByIsActiveTrue()
                .orElseThrow(() -> new BusinessException(ErrorCode.REGISTRATION_NOT_OPEN));
        String semesterId = activePeriod.getSemester().getId();

        // Sử dụng phương thức với @EntityGraph
        List<Course> courses = courseRepository.findAllBySemesterId(semesterId);

        // Map sang DTO
        return courses.stream()
                .map(courseMapper::toCourseResponse)
                .collect(Collectors.toList());
    }

    public MyScheduleResponse getMySchedule(String studentId, String semesterId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndCourseSemesterId(studentId, semesterId);

        List<MyScheduleResponse.ScheduleItem> scheduleItems = enrollments.stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();
                    return MyScheduleResponse.ScheduleItem.builder()
                            .courseCode(course.getCourseCode())
                            .subjectName(course.getSubject().getName())
                            .credits(course.getSubject().getCredits())
                            .lecturerName(course.getLecturer().getUser().getFirstname() + " " + course.getLecturer().getUser().getLastname())
                            .scheduleInfo(course.getScheduleInfo())
                            .build();
                })
                .collect(Collectors.toList());

        return MyScheduleResponse.builder()
                .studentCode(student.getStudentCode())
                .studentName(student.getUser().getFirstname() + " " + student.getUser().getLastname())
                .semesterName(enrollments.isEmpty() ? "N/A" : enrollments.get(0).getCourse().getSemester().getName())
                .scheduleItems(scheduleItems)
                .build();
    }
}